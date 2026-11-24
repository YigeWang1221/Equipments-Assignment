package com.zjgs.equip.common;

public enum BorrowStatus {
    APPLYING(0, "申请中"),
    BORROWED(1, "已借出"),
    RETURNED(2, "已归还"),
    REJECTED(3, "已拒绝"),
    CANCELLED(4, "已取消");

    private final int code;
    private final String label;

    BorrowStatus(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static BorrowStatus fromCode(int code) {
        for (BorrowStatus value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new BusinessException("未知借用状态: " + code);
    }
}
