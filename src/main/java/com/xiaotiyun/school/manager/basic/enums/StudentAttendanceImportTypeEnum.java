package com.xiaotiyun.school.manager.basic.enums;

public enum StudentAttendanceImportTypeEnum {
    IN(1, "入校"),
    OUT(2, "出校");

    private int code;
    private String value;

    StudentAttendanceImportTypeEnum(int code, String value) {
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
        for (StudentAttendanceImportTypeEnum ele : values()) {
            if (ele.getCode() == code) return ele.getValue();
        }
        return null;
    }

    public static Integer getCode(String value) {
        for (StudentAttendanceImportTypeEnum ele : values()) {
            if (ele.getValue().equals(value)) return ele.getCode();
        }
        return null;
    }

    public static StudentAttendanceImportTypeEnum getByValue(String value) {
        for (StudentAttendanceImportTypeEnum ele : values()) {
            if (ele.getValue().equals(value)) return ele;
        }
        return null;
    }
}
