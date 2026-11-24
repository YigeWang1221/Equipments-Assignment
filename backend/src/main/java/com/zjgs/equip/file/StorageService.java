package com.zjgs.equip.file;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件存储抽象，目前提供本地磁盘与 MinIO 两种实现。
 */
public interface StorageService {

    /** 存储类型标识：local / minio。 */
    String type();

    /**
     * 保存文件。
     *
     * @param file 上传文件
     * @param key  相对存储路径，例如 2026/11/24/uuid.jpg
     * @return 存储结果（相对 key + 可访问 URL）
     */
    StoredFile store(MultipartFile file, String key) throws IOException;

    /** 读取文件，供下载/代理使用。 */
    Resource load(String key) throws IOException;

    record StoredFile(String key, String url) {
    }
}
