package com.zjgs.equip.file;

import com.zjgs.equip.common.BusinessException;
import com.zjgs.equip.common.ErrorCode;
import com.zjgs.equip.config.StorageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 本地磁盘存储：{localDir}/yyyy/MM/dd/uuid.ext。
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "app.storage", name = "type", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {
    private final StorageProperties properties;
    private final Path baseDir;

    public LocalStorageService(StorageProperties properties) {
        this.properties = properties;
        this.baseDir = Paths.get(properties.getLocalDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(baseDir);
        } catch (IOException e) {
            log.warn("Cannot create local storage directory {}: {}", baseDir, e.getMessage());
        }
    }

    @Override
    public String type() {
        return "local";
    }

    @Override
    public StoredFile store(MultipartFile file, String key) throws IOException {
        Path target = resolve(key);
        Files.createDirectories(target.getParent());
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return new StoredFile(key, buildUrl(key));
    }

    @Override
    public Resource load(String key) {
        Path target = resolve(key);
        if (!Files.exists(target) || !Files.isReadable(target)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "文件不存在");
        }
        return new FileSystemResource(target);
    }

    public Path baseDir() {
        return baseDir;
    }

    private Path resolve(String key) {
        Path target = baseDir.resolve(key).normalize();
        if (!target.startsWith(baseDir)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "非法的文件路径");
        }
        return target;
    }

    private String buildUrl(String key) {
        String base = properties.getPublicBaseUrl();
        if (base == null || base.isBlank()) {
            base = "/files";
        }
        return (base.endsWith("/") ? base.substring(0, base.length() - 1) : base) + "/" + key;
    }
}
