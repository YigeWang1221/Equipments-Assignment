package com.zjgs.equip.common;

public enum ClassType {
    ARM(1, "嵌入式"),
    IOT(2, "物联网");

    private final int code;
    private final String label;

    ClassType(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static ClassType fromCode(int code) {
        for (ClassType value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new BusinessException("未知班级类型: " + code);
    }
}
