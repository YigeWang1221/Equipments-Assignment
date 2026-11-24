package com.zjgs.equip.common;

public enum SubmitStatus {
    NOT_STARTED(0, "未开始"),
    DRAFT(1, "草稿"),
    SUBMITTED(2, "已提交"),
    GRADED(3, "已批改");

    private final int code;
    private final String label;

    SubmitStatus(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static SubmitStatus fromCode(int code) {
        for (SubmitStatus value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return NOT_STARTED;
    }
}
