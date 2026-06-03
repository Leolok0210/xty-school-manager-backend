package com.xiaotiyun.school.manager.basic.enums;

public enum ExportHeaderTypeEnum {
    STUDENT_INFO(1, "学生资料");

    private int code;
    private String value;

    ExportHeaderTypeEnum(int code, String value) {
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
        for (ExportHeaderTypeEnum ele : values()) {
            if (ele.getCode() == code) return ele.getValue();
        }
        return null;
    }
}
