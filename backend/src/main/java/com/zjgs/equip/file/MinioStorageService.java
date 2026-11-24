package com.zjgs.equip.file;

import com.zjgs.equip.common.BusinessException;
import com.zjgs.equip.common.ErrorCode;
import com.zjgs.equip.config.StorageProperties;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * MinIO 对象存储实现。对象 URL 默认使用 endpoint/bucket/key。
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "app.storage", name = "type", havingValue = "minio")
public class MinioStorageService implements StorageService {
    private final MinioClient client;
    private final String bucket;
    private final String endpoint;
    private volatile boolean bucketReady = false;

    public MinioStorageService(StorageProperties properties) {
        StorageProperties.Minio minio = properties.getMinio();
        this.client = MinioClient.builder()
                .endpoint(minio.getEndpoint())
                .credentials(minio.getAccessKey(), minio.getSecretKey())
                .build();
        this.bucket = minio.getBucket();
        this.endpoint = trimTrailingSlash(minio.getEndpoint());
    }

    @Override
    public String type() {
        return "minio";
    }

    @Override
    public StoredFile store(MultipartFile file, String key) throws IOException {
        ensureBucket();
        try (InputStream in = file.getInputStream()) {
            client.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .stream(in, file.getSize(), -1)
                    .contentType(contentType(file))
                    .build());
        } catch (Exception e) {
            log.error("MinIO upload failed for {}", key, e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "文件上传失败");
        }
        return new StoredFile(key, buildUrl(key));
    }

    @Override
    public Resource load(String key) throws IOException {
        try {
            GetObjectResponse response = client.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .build());
            return new InputStreamResource(response);
        } catch (Exception e) {
            log.warn("MinIO download failed for {}: {}", key, e.getMessage());
            throw new BusinessException(ErrorCode.NOT_FOUND, "文件不存在");
        }
    }

    private void ensureBucket() {
        if (bucketReady) {
            return;
        }
        synchronized (this) {
            if (bucketReady) {
                return;
            }
            try {
                boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
                if (!exists) {
                    client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                    log.info("Created MinIO bucket {}", bucket);
                }
                bucketReady = true;
            } catch (Exception e) {
                log.error("MinIO bucket {} unavailable", bucket, e);
                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "对象存储不可用");
            }
        }
    }

    private String buildUrl(String key) {
        return endpoint + "/" + bucket + "/" + key;
    }

    private String contentType(MultipartFile file) {
        String type = file.getContentType();
        return type == null || type.isBlank() ? "application/octet-stream" : type;
    }

    private String trimTrailingSlash(String value) {
        if (value == null) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
