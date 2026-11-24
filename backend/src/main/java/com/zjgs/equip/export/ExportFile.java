package com.zjgs.equip.export;

/**
 * 导出结果：文件字节与下载文件名。
 */
public record ExportFile(byte[] content, String filename, String contentType) {
}
