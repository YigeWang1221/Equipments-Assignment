package com.zjgs.equip.export;

import com.zjgs.equip.clazz.entity.ClassGroup;
import com.zjgs.equip.clazz.entity.ClassInfo;
import com.zjgs.equip.homework.entity.StuWork;
import com.zjgs.equip.homework.entity.TeacherWork;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfExportServiceTest {

    @Test
    void renderSubmissionProducesPdf() throws Exception {
        TeacherWork work = new TeacherWork();
        work.setWorkName("STM32 环境搭建与点灯实验");
        work.setContentHtml("<h2>实验目标</h2><p>完成 Keil 工程创建，控制 LED 闪烁。<br>注意：<strong>安全第一</strong></p>");

        StuWork stuWork = new StuWork();
        stuWork.setId(1L);
        stuWork.setGroupId(1L);
        stuWork.setWorkDoc("<h2>实验过程</h2><p>接线完成，测试通过。</p><script>alert(1)</script>");
        stuWork.setVideoUrl("https://example.com/video/1");
        stuWork.setZipUrl("https://example.com/zip/1.zip");
        stuWork.setScore(new BigDecimal("92.50"));
        stuWork.setSubmittedAt(LocalDateTime.now());

        ClassInfo classInfo = new ClassInfo();
        classInfo.setId(1L);
        classInfo.setClassName("2024物联网课程1班");

        ClassGroup group = new ClassGroup();
        group.setId(1L);
        group.setGroupName("第1组");
        group.setMembersText("2023000001 王亦格, 2023000002 陈一鸣");

        byte[] pdf = new PdfExportService().renderSubmission(stuWork, work, classInfo, group);
        assertTrue(pdf.length > 500);
        assertTrue(new String(pdf, 0, 4).equals("%PDF"));
        try (org.apache.pdfbox.pdmodel.PDDocument document =
                     org.apache.pdfbox.pdmodel.PDDocument.load(pdf)) {
            String text = new org.apache.pdfbox.text.PDFTextStripper().getText(document);
            assertTrue(text.contains("STM32"));
            assertTrue(text.contains("实验目标"), "Chinese text should be extractable when font is embedded");
            assertTrue(text.contains("第1组"));
        }
    }

    @Test
    void sanitizerDropsScriptsAndBalancesTags() {
        String html = HtmlSanitizer.sanitize("<p onclick=\"x()\">hi<script>alert(1)</script><b>bold");
        assertTrue(html.contains("&lt;script&gt;") || !html.contains("alert(1)"));
        assertTrue(html.endsWith("</b></p>"));
    }
}
