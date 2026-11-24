package com.zjgs.equip.export;

import com.zjgs.equip.homework.entity.TeacherWork;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * 班级成绩单 Excel 导出。列：学号、姓名、小组、各次作业成绩、平均分。
 */
@Component
public class GradeExcelExporter {

    public byte[] export(String className, List<TeacherWork> works, List<GradeRow> rows) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("成绩单");
            CellStyle headerStyle = headerStyle(workbook);
            CellStyle textStyle = bodyStyle(workbook, false);
            CellStyle numberStyle = bodyStyle(workbook, true);

            Row header = sheet.createRow(0);
            header.setHeightInPoints(24);
            String[] fixedHeaders = {"学号", "姓名", "小组"};
            for (int i = 0; i < fixedHeaders.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(fixedHeaders[i]);
                cell.setCellStyle(headerStyle);
            }
            for (int i = 0; i < works.size(); i++) {
                Cell cell = header.createCell(3 + i);
                cell.setCellValue(works.get(i).getWorkName());
                cell.setCellStyle(headerStyle);
            }
            int averageColumn = 3 + works.size();
            Cell averageHeader = header.createCell(averageColumn);
            averageHeader.setCellValue("平均分");
            averageHeader.setCellStyle(headerStyle);

            int rowIndex = 1;
            for (GradeRow gradeRow : rows) {
                Row row = sheet.createRow(rowIndex++);
                row.setHeightInPoints(20);
                fillText(row, 0, gradeRow.stuId() == null ? "" : String.valueOf(gradeRow.stuId()), textStyle);
                fillText(row, 1, gradeRow.stuName(), textStyle);
                fillText(row, 2, gradeRow.groupName(), textStyle);
                for (int i = 0; i < works.size(); i++) {
                    BigDecimal score = gradeRow.scores().get(works.get(i).getId());
                    Cell cell = row.createCell(3 + i);
                    cell.setCellStyle(numberStyle);
                    if (score != null) {
                        cell.setCellValue(score.doubleValue());
                    }
                }
                Cell averageCell = row.createCell(averageColumn);
                averageCell.setCellStyle(numberStyle);
                if (gradeRow.average() != null) {
                    averageCell.setCellValue(gradeRow.average().doubleValue());
                }
            }

            sheet.setColumnWidth(0, 16 * 256);
            sheet.setColumnWidth(1, 12 * 256);
            sheet.setColumnWidth(2, 14 * 256);
            for (int i = 0; i < works.size(); i++) {
                sheet.setColumnWidth(3 + i, 20 * 256);
            }
            sheet.setColumnWidth(averageColumn, 12 * 256);
            sheet.createFreezePane(3, 1);
            sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(
                    0, Math.max(rows.size(), 1), 0, averageColumn));

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("生成成绩单失败", e);
        }
    }

    private void fillText(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value == null ? "" : value);
        cell.setCellStyle(style);
    }

    private CellStyle headerStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        applyBorders(style);
        return style;
    }

    private CellStyle bodyStyle(Workbook workbook, boolean number) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(number ? HorizontalAlignment.CENTER : HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        if (number) {
            style.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
        }
        applyBorders(style);
        return style;
    }

    private void applyBorders(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    /**
     * 一行成绩数据。
     */
    public record GradeRow(Long stuId, String stuName, String groupName,
                           java.util.Map<Long, BigDecimal> scores, BigDecimal average) {
    }
}
