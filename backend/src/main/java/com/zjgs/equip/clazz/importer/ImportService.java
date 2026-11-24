package com.zjgs.equip.clazz.importer;

import com.zjgs.equip.clazz.dto.ImportResult;
import com.zjgs.equip.clazz.entity.ClassGroup;
import com.zjgs.equip.clazz.service.GroupService;
import com.zjgs.equip.clazz.support.ClassGuard;
import com.zjgs.equip.common.BusinessException;
import com.zjgs.equip.user.service.StudentService;
import com.zjgs.equip.ws.RealtimePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生/小组 Excel 导入与模板生成。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImportService {
    private static final String COL_STU_ID = "学号";
    private static final String COL_NAME = "姓名";
    private static final String COL_ADMIN_CLASS = "行政班级";
    private static final String COL_COLLEGE = "学院";
    private static final String COL_TEACHING_CLASS = "教学班级";
    private static final String COL_GROUP_NAME = "小组名称";
    private static final String COL_PHONE = "电话";

    private final ClassGuard classGuard;
    private final StudentService studentService;
    private final GroupService groupService;
    private final RealtimePublisher realtimePublisher;

    public ImportResult importStudents(MultipartFile file, Long classId, Long teacherId) {
        classGuard.requireOwnedClass(classId, teacherId);
        List<ImportRow> rows = readRows(file, List.of(COL_STU_ID, COL_NAME));

        List<String> errors = new ArrayList<>();
        int success = 0;
        for (ImportRow row : rows) {
            try {
                String stuIdText = row.values().getOrDefault(COL_STU_ID, "");
                Long stuId = parseStuId(stuIdText);
                String name = row.values().getOrDefault(COL_NAME, "");
                String teachingClass = blankToNull(row.values().get(COL_TEACHING_CLASS));
                studentService.importUpsert(stuId, name, row.values().get(COL_PHONE),
                        row.values().get(COL_COLLEGE), row.values().get(COL_ADMIN_CLASS),
                        classId, teachingClass);

                String groupName = blankToNull(row.values().get(COL_GROUP_NAME));
                if (groupName != null) {
                    ClassGroup group = groupService.findOrCreateGroup(classId, groupName, teachingClass);
                    groupService.assignStudentToGroup(group.getId(), stuId);
                }
                success++;
            } catch (Exception e) {
                errors.add("第" + row.rowNumber() + "行: " + messageOf(e));
            }
        }
        if (success > 0) {
            realtimePublisher.publishClassEvent(classId, "group.updated",
                    Map.of("classId", classId, "imported", success));
        }
        return ImportResult.builder()
                .successCount(success)
                .failureCount(errors.size())
                .errors(errors)
                .build();
    }

    public ImportResult importGroups(MultipartFile file, Long classId, Long teacherId) {
        classGuard.requireOwnedClass(classId, teacherId);
        List<ImportRow> rows = readRows(file, List.of(COL_GROUP_NAME, COL_STU_ID));

        List<String> errors = new ArrayList<>();
        int success = 0;
        for (ImportRow row : rows) {
            try {
                String groupName = blankToNull(row.values().get(COL_GROUP_NAME));
                if (groupName == null) {
                    throw new BusinessException("小组名称不能为空");
                }
                Long stuId = parseStuId(row.values().getOrDefault(COL_STU_ID, ""));
                String teachingClass = blankToNull(row.values().get(COL_TEACHING_CLASS));
                ClassGroup group = groupService.findOrCreateGroup(classId, groupName, teachingClass);
                studentService.importUpsert(stuId, row.values().get(COL_NAME), null, null, null,
                        classId, teachingClass);
                groupService.assignStudentToGroup(group.getId(), stuId);
                success++;
            } catch (Exception e) {
                errors.add("第" + row.rowNumber() + "行: " + messageOf(e));
            }
        }
        if (success > 0) {
            realtimePublisher.publishClassEvent(classId, "group.updated",
                    Map.of("classId", classId, "imported", success));
        }
        return ImportResult.builder()
                .successCount(success)
                .failureCount(errors.size())
                .errors(errors)
                .build();
    }

    public byte[] studentTemplate() {
        return buildTemplate(new String[]{COL_STU_ID, COL_NAME, COL_ADMIN_CLASS, COL_COLLEGE,
                COL_TEACHING_CLASS, COL_GROUP_NAME, COL_PHONE},
                new int[]{16, 14, 18, 24, 18, 16, 18});
    }

    public byte[] groupTemplate() {
        return buildTemplate(new String[]{COL_GROUP_NAME, COL_TEACHING_CLASS, COL_STU_ID, COL_NAME},
                new int[]{16, 20, 16, 14});
    }

    private List<ImportRow> readRows(MultipartFile file, List<String> requiredHeaders) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请上传Excel文件");
        }
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            if (workbook.getNumberOfSheets() == 0) {
                throw new BusinessException("Excel中没有工作表");
            }
            Sheet sheet = workbook.getSheetAt(0);
            int headerRowIndex = sheet.getFirstRowNum();
            Row headerRow = sheet.getRow(headerRowIndex);
            if (headerRow == null) {
                throw new BusinessException("Excel缺少表头");
            }
            DataFormatter formatter = new DataFormatter();
            Map<String, Integer> headerIndex = new HashMap<>();
            for (Cell cell : headerRow) {
                String header = normalizeHeader(formatter.formatCellValue(cell));
                if (!header.isBlank()) {
                    headerIndex.put(header, cell.getColumnIndex());
                }
            }
            for (String required : requiredHeaders) {
                if (!headerIndex.containsKey(required)) {
                    throw new BusinessException("Excel模板缺少列: " + required);
                }
            }

            List<ImportRow> rows = new ArrayList<>();
            for (int rowIndex = headerRowIndex + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }
                Map<String, String> values = new LinkedHashMap<>();
                boolean blank = true;
                for (Map.Entry<String, Integer> entry : headerIndex.entrySet()) {
                    String value = cellText(row, entry.getValue(), formatter);
                    values.put(entry.getKey(), value);
                    if (!value.isBlank()) {
                        blank = false;
                    }
                }
                if (!blank) {
                    rows.add(new ImportRow(rowIndex + 1, values));
                }
            }
            return rows;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Failed to parse excel file: {}", e.getMessage());
            throw new BusinessException("Excel文件解析失败，请使用模板重新导入");
        }
    }

    private String cellText(Row row, Integer columnIndex, DataFormatter formatter) {
        if (columnIndex == null) {
            return "";
        }
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            double value = cell.getNumericCellValue();
            if (value == Math.rint(value) && Math.abs(value) < 1e15) {
                return BigDecimal.valueOf(value).toBigInteger().toString();
            }
        }
        return formatter.formatCellValue(cell).trim();
    }

    private byte[] buildTemplate(String[] headers, int[] widths) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("导入模板");
            Row headerRow = sheet.createRow(0);
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(style);
                sheet.setColumnWidth(i, widths[i] * 256);
            }
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new BusinessException("模板生成失败");
        }
    }

    private Long parseStuId(String value) {
        String text = value == null ? "" : value.trim();
        if (text.endsWith(".0")) {
            text = text.substring(0, text.length() - 2);
        }
        if (!text.matches("\\d{10}")) {
            throw new BusinessException("学号必须为10位数字");
        }
        return Long.valueOf(text);
    }

    private String normalizeHeader(String header) {
        return header == null ? "" : header.replace("\uFEFF", "").trim();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String messageOf(Exception e) {
        String message = e.getMessage();
        return message == null || message.isBlank() ? "数据格式错误" : message;
    }

    private record ImportRow(int rowNumber, Map<String, String> values) {
    }
}
