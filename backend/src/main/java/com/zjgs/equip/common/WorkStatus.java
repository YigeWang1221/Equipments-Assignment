package com.zjgs.equip.common;

public enum WorkStatus {
    NOT_STARTED(0, "未开始"),
    ONGOING(1, "进行中"),
    ENDED(2, "截止");

    private final int code;
    private final String label;

    WorkStatus(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static WorkStatus fromCode(int code) {
        for (WorkStatus value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return NOT_STARTED;
    }
}
