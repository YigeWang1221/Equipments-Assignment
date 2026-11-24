package com.zjgs.equip.export;

import com.zjgs.equip.homework.entity.TeacherWork;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GradeExcelExporterTest {

    @Test
    void exportWritesHeaderAndScores() throws Exception {
        TeacherWork work = new TeacherWork();
        work.setId(1L);
        work.setWorkName("实验一");

        GradeExcelExporter.GradeRow row = new GradeExcelExporter.GradeRow(
                2023000001L, "王亦格", "第1组", Map.of(1L, new BigDecimal("92.5")), new BigDecimal("92.50"));

        byte[] content = new GradeExcelExporter().export("测试班", List.of(work), List.of(row));
        assertTrue(content.length > 0);
        try (var workbook = WorkbookFactory.create(new ByteArrayInputStream(content))) {
            var sheet = workbook.getSheetAt(0);
            assertEquals("学号", sheet.getRow(0).getCell(0).getStringCellValue());
            assertEquals("实验一", sheet.getRow(0).getCell(3).getStringCellValue());
            assertEquals(92.5, sheet.getRow(1).getCell(3).getNumericCellValue());
            assertEquals("王亦格", sheet.getRow(1).getCell(1).getStringCellValue());
        }
    }
}
