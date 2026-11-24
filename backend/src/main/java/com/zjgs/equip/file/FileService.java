package com.zjgs.equip.file;

import com.zjgs.equip.auth.CurrentUser;
import com.zjgs.equip.common.BusinessException;
import com.zjgs.equip.common.ErrorCode;
import com.zjgs.equip.file.dto.FileVO;
import com.zjgs.equip.file.entity.FileObject;
import com.zjgs.equip.file.mapper.FileObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

/**
 * 文件上传与元数据管理。
 */
@Slf4j
@Service
public class FileService {
    private static final long MAX_SIZE = 320L * 1024 * 1024;
    private static final DateTimeFormatter DATE_PATH = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final FileObjectMapper fileObjectMapper;
    private final StorageService storageService;
    private final CurrentUser currentUser;

    public FileService(FileObjectMapper fileObjectMapper, StorageService storageService, CurrentUser currentUser) {
        this.fileObjectMapper = fileObjectMapper;
        this.storageService = storageService;
        this.currentUser = currentUser;
    }

    public FileVO upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("上传文件不能超过320MB");
        }
        String originalName = safeOriginalName(file.getOriginalFilename());
        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }
        String key = LocalDate.now().format(DATE_PATH) + "/" + UUID.randomUUID().toString().replace("-", "")
                + "." + extension(originalName);

        StorageService.StoredFile stored;
        try {
            stored = storageService.store(file, key);
        } catch (IOException e) {
            log.error("Store file failed: {}", originalName, e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "文件保存失败");
        }

        FileObject object = new FileObject();
        object.setStorageType(storageService.type());
        object.setStorageKey(stored.key());
        object.setOriginalName(originalName);
        object.setContentType(contentType);
        object.setSizeBytes(file.getSize());
        object.setUrl(stored.url());
        object.setUploaderId(currentUser.require().userId());
        object.setCreatedAt(LocalDateTime.now());
        fileObjectMapper.insert(object);
        return toVO(object);
    }

    public FileVO get(Long id) {
        return toVO(require(id));
    }

    public FileObject require(Long id) {
        FileObject object = fileObjectMapper.selectById(id);
        if (object == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "文件不存在");
        }
        return object;
    }

    public Resource load(FileObject object) {
        try {
            return storageService.load(object.getStorageKey());
        } catch (IOException e) {
            log.error("Load file {} failed", object.getId(), e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "文件读取失败");
        }
    }

    public FileVO toVO(FileObject object) {
        return FileVO.builder()
                .id(object.getId())
                .url(object.getUrl())
                .name(object.getOriginalName())
                .size(object.getSizeBytes())
                .contentType(object.getContentType())
                .createdAt(object.getCreatedAt())
                .build();
    }

    private String safeOriginalName(String name) {
        if (name == null || name.isBlank()) {
            return "file";
        }
        String normalized = name.replace('\\', '/');
        int slash = normalized.lastIndexOf('/');
        String value = slash >= 0 ? normalized.substring(slash + 1) : normalized;
        value = value.replaceAll("[\\r\\n\\t\\x00-\\x1f\\x7f]", "_").trim();
        if (value.length() > 255) {
            value = value.substring(value.length() - 255);
        }
        return value.isBlank() ? "file" : value;
    }

    private String extension(String name) {
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) {
            return "bin";
        }
        String ext = name.substring(dot + 1).toLowerCase(Locale.ROOT);
        return ext.matches("[a-z0-9]{1,10}") ? ext : "bin";
    }
}
