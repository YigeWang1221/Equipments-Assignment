package com.zjgs.equip.file;

import com.zjgs.equip.common.ApiResponse;
import com.zjgs.equip.file.dto.FileVO;
import com.zjgs.equip.file.entity.FileObject;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping
    public ApiResponse<FileVO> upload(@RequestParam(value = "file", required = false) MultipartFile file) {
        return ApiResponse.ok(fileService.upload(file));
    }

    @GetMapping("/{id}")
    public ApiResponse<FileVO> get(@PathVariable Long id) {
        return ApiResponse.ok(fileService.get(id));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        FileObject object = fileService.require(id);
        Resource resource = fileService.load(object);
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (object.getContentType() != null) {
            try {
                mediaType = MediaType.parseMediaType(object.getContentType());
            } catch (Exception ignored) {
                // 保底使用二进制流
            }
        }
        ResponseEntity.BodyBuilder builder = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(object.getOriginalName(), StandardCharsets.UTF_8)
                        .build().toString())
                .contentType(mediaType);
        if (object.getSizeBytes() != null && object.getSizeBytes() > 0) {
            builder.contentLength(object.getSizeBytes());
        }
        return builder.body(resource);
    }
}
