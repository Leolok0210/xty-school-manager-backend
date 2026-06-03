package com.xiaotiyun.school.manager.basic.enums;

public enum TeacherAttendanceStatusEnum {
    NORMAL(1, "正常"),
    BE_LATE(2, "遲到"),
    LEAVE_EARLY(3, "早退"),
    MISSING_CARD(4, "缺卡");

    private int code;
    private String value;

    TeacherAttendanceStatusEnum(int code, String value) {
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
        for (TeacherAttendanceStatusEnum ele : values()) {
            if (ele.getCode() == code) return ele.getValue();
        }
        return null;
    }

    public static Integer getCode(String value) {
        for (TeacherAttendanceStatusEnum ele : values()) {
            if (ele.getValue().equals(value)) return ele.getCode();
        }
        return null;
    }

    public static TeacherAttendanceStatusEnum getByValue(String value) {
        for (TeacherAttendanceStatusEnum ele : values()) {
            if (ele.getValue().equals(value)) return ele;
        }
        return null;
    }
}
