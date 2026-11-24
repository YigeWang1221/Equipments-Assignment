package com.zjgs.equip.export;

import com.zjgs.equip.auth.CurrentUser;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/exports")
public class ExportController {
    private final ExportService exportService;
    private final CurrentUser currentUser;

    public ExportController(ExportService exportService, CurrentUser currentUser) {
        this.exportService = exportService;
        this.currentUser = currentUser;
    }

    @GetMapping("/classes/{classId}/grades.xlsx")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<byte[]> classGrades(@PathVariable Long classId) {
        return download(exportService.exportClassGrades(classId, currentUser.require()));
    }

    @GetMapping("/submissions/{id}.pdf")
    public ResponseEntity<byte[]> submissionPdf(@PathVariable Long id) {
        return download(exportService.exportSubmissionPdf(id, currentUser.require()));
    }

    @GetMapping("/works/{workId}/pdfs.zip")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<byte[]> workPdfZip(@PathVariable Long workId) {
        return download(exportService.exportWorkPdfZip(workId, currentUser.require()));
    }

    private ResponseEntity<byte[]> download(ExportFile file) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(file.filename(), StandardCharsets.UTF_8)
                        .build().toString())
                .contentType(MediaType.parseMediaType(file.contentType()))
                .contentLength(file.content().length)
                .body(file.content());
    }
}
