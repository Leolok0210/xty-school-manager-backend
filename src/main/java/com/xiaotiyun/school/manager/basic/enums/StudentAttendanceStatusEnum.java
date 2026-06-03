package com.xiaotiyun.school.manager.basic.enums;

public enum StudentAttendanceStatusEnum {
    NORMAL(0, "正常"),
    BE_LATE(1, "遲到"),
    LEAVE_EARLY(2, "早退"),
    MISSING_CARD(3, "缺卡"),
    DATA_EXCEPTION(4, "數據異常");

    private int code;
    private String value;

    StudentAttendanceStatusEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static String getValue(int code) {
        for (StudentAttendanceStatusEnum ele : values()) {
            if (ele.getCode() == code) return ele.getValue();
        }
        return null;
    }

    public static Integer getCode(String value) {
        for (StudentAttendanceStatusEnum ele : values()) {
            if (ele.getValue().equals(value)) return ele.getCode();
        }
        return null;
    }

    public static StudentAttendanceStatusEnum getByValue(String value) {
        for (StudentAttendanceStatusEnum ele : values()) {
            if (ele.getValue().equals(value)) return ele;
        }
        return null;
    }
}
