package com.zjgs.equip.clazz.controller;

import com.zjgs.equip.auth.CurrentUser;
import com.zjgs.equip.clazz.dto.ImportResult;
import com.zjgs.equip.clazz.importer.ImportService;
import com.zjgs.equip.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/imports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TEACHER')")
public class ImportController {
    private static final MediaType XLSX = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final ImportService importService;
    private final CurrentUser currentUser;

    @PostMapping(value = "/students", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ImportResult> importStudents(@RequestParam("file") MultipartFile file,
                                                    @RequestParam("classId") Long classId) {
        return ApiResponse.ok(importService.importStudents(file, classId, currentUser.teacherId()));
    }

    @PostMapping(value = "/groups", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ImportResult> importGroups(@RequestParam("file") MultipartFile file,
                                                  @RequestParam("classId") Long classId) {
        return ApiResponse.ok(importService.importGroups(file, classId, currentUser.teacherId()));
    }

    @GetMapping("/templates/students.xlsx")
    public ResponseEntity<byte[]> studentTemplate() {
        return templateResponse(importService.studentTemplate(), "students.xlsx");
    }

    @GetMapping("/templates/groups.xlsx")
    public ResponseEntity<byte[]> groupTemplate() {
        return templateResponse(importService.groupTemplate(), "groups.xlsx");
    }

    private ResponseEntity<byte[]> templateResponse(byte[] data, String fileName) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(XLSX)
                .body(data);
    }
}
