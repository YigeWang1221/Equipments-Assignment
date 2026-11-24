package com.zjgs.equip.export;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.zjgs.equip.clazz.entity.ClassGroup;
import com.zjgs.equip.clazz.entity.ClassInfo;
import com.zjgs.equip.common.BusinessException;
import com.zjgs.equip.common.ErrorCode;
import com.zjgs.equip.homework.entity.StuWork;
import com.zjgs.equip.homework.entity.TeacherWork;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 单份小组作业 PDF 渲染。CJK 字体从 classpath:/fonts/ 加载，缺失时回退 Helvetica。
 */
@Slf4j
@Component
public class PdfExportService {
    private static final String FONT_FAMILY = "NotoSansSC";
    private static final List<String> FONT_CANDIDATES = List.of(
            "fonts/NotoSansSC-Regular.ttf",
            "fonts/NotoSansSC-Regular.otf");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static volatile boolean fontWarned = false;

    public byte[] renderSubmission(StuWork stuWork, TeacherWork work, ClassInfo classInfo, ClassGroup group) {
        String html = buildHtml(stuWork, work, classInfo, group);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            boolean hasCjkFont = registerFont(builder);
            if (!hasCjkFont && !fontWarned) {
                fontWarned = true;
                log.warn("No CJK font found under classpath:/fonts/ (expected {}), fallback to Helvetica; "
                        + "Chinese characters may not render correctly.", FONT_CANDIDATES);
            }
            builder.useFastMode();
            builder.withHtmlContent(html, "file:///");
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Render submission PDF failed, stuWorkId={}", stuWork.getId(), e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "PDF生成失败");
        }
    }

    private boolean registerFont(PdfRendererBuilder builder) {
        for (String path : FONT_CANDIDATES) {
            ClassPathResource resource = new ClassPathResource(path);
            if (resource.exists()) {
                builder.useFont(() -> {
                    try {
                        return resource.getInputStream();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }, FONT_FAMILY);
                return true;
            }
        }
        return false;
    }

    private String buildHtml(StuWork stuWork, TeacherWork work, ClassInfo classInfo, ClassGroup group) {
        String fontStack = "'" + FONT_FAMILY + "', Helvetica, Arial, sans-serif";
        String videoUrl = trimToNull(stuWork.getVideoUrl());
        String zipUrl = trimToNull(stuWork.getZipUrl());
        String qrDataUri = QrCodeUtil.pngDataUri(videoUrl);

        StringBuilder html = new StringBuilder(8192);
        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"/><style>")
                .append("@page { size: A4; margin: 16mm 15mm 18mm 15mm; }")
                .append("body { font-family: ").append(fontStack).append("; font-size: 10.5pt; color: #222; line-height: 1.55; }")
                .append("h1 { font-size: 19pt; text-align: center; margin: 0 0 14pt 0; letter-spacing: 2pt; }")
                .append("h2 { font-size: 13pt; margin: 16pt 0 6pt 0; padding-left: 6pt; border-left: 3pt solid #2f6fdd; }")
                .append("table.info { width: 100%; border-collapse: collapse; margin-bottom: 6pt; }")
                .append("table.info th, table.info td { border: 0.6pt solid #b9c2cf; padding: 5pt 7pt; vertical-align: top; }")
                .append("table.info th { width: 16%; background: #f1f4f9; text-align: left; font-weight: bold; }")
                .append(".rich { padding: 2pt 0; word-wrap: break-word; }")
                .append(".rich h1, .rich h2, .rich h3 { border: none; padding: 0; margin: 10pt 0 4pt 0; font-size: 12pt; }")
                .append(".rich p { margin: 4pt 0; }")
                .append(".rich ul, .rich ol { margin: 4pt 0 4pt 16pt; padding: 0; }")
                .append(".rich table { border-collapse: collapse; width: 100%; }")
                .append(".rich th, .rich td { border: 0.6pt solid #b9c2cf; padding: 4pt; }")
                .append(".rich img { max-width: 100%; }")
                .append(".muted { color: #888; }")
                .append(".qr { margin-top: 4pt; }")
                .append("a { color: #1f5fbf; text-decoration: underline; }")
                .append(".footer { margin-top: 24pt; font-size: 8.5pt; color: #999; text-align: center; }")
                .append("</style></head><body>");

        html.append("<h1>课程作业报告</h1>");
        html.append("<table class=\"info\">");
        html.append(infoRow("作业名称", escape(work.getWorkName())));
        html.append(infoRow("班级", escape(classInfo.getClassName())));
        html.append(infoRow("小组", escape(group == null ? "未分组" : group.getGroupName())));
        html.append(infoRow("成员", escape(group == null ? "" : group.getMembersText())));
        html.append(infoRow("成绩", stuWork.getScore() == null ? "未评分" : formatScore(stuWork.getScore())));
        html.append(infoRow("提交时间", stuWork.getSubmittedAt() == null ? "未提交"
                : stuWork.getSubmittedAt().format(TIME_FORMAT)));
        html.append("</table>");

        html.append("<h2>作业要求</h2><div class=\"rich\">")
                .append(renderRich(work.getContentHtml()))
                .append("</div>");

        html.append("<h2>小组提交内容</h2><div class=\"rich\">")
                .append(renderRich(stuWork.getWorkDoc()))
                .append("</div>");

        html.append("<h2>演示视频</h2>");
        if (videoUrl == null) {
            html.append("<p class=\"muted\">未提交视频</p>");
        } else {
            html.append("<p>视频链接：<a href=\"").append(escape(videoUrl)).append("\">")
                    .append(escape(videoUrl)).append("</a></p>");
            if (qrDataUri != null) {
                html.append("<div class=\"qr\"><img src=\"").append(qrDataUri)
                        .append("\" width=\"150\" height=\"150\" alt=\"video qr\"/></div>")
                        .append("<p class=\"muted\">扫码查看演示视频</p>");
            }
        }

        html.append("<h2>附件</h2>");
        if (zipUrl == null) {
            html.append("<p class=\"muted\">无附件</p>");
        } else {
            html.append("<p>工程/附件下载：<a href=\"").append(escape(zipUrl)).append("\">")
                    .append(escape(zipUrl)).append("</a></p>");
        }

        html.append("<div class=\"footer\">由实验教学管理平台自动生成 · ")
                .append(java.time.LocalDateTime.now().format(TIME_FORMAT))
                .append("</div>");
        html.append("</body></html>");
        return html.toString();
    }

    private String infoRow(String label, String value) {
        return "<tr><th>" + label + "</th><td>" + value + "</td></tr>";
    }

    private String renderRich(String html) {
        String sanitized = HtmlSanitizer.sanitize(html);
        return sanitized.isBlank() ? "<p class=\"muted\">无内容</p>" : sanitized;
    }

    private String formatScore(BigDecimal score) {
        return score.stripTrailingZeros().toPlainString();
    }

    private String escape(String value) {
        return HtmlSanitizer.escape(value);
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
