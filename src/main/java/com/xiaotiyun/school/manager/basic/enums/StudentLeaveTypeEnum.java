package com.xiaotiyun.school.manager.basic.enums;

public enum StudentLeaveTypeEnum {
    LEAVE(1, "請假", "Leave", "Licença"),
    ABSENT(2, "缺席", "Absent", "Ausente"),
    LATE(3, "遲到", "Late", "Atrasado");

    private int code;
    private String value;
    private String englishValue;
    private String portugueseValue;

    StudentLeaveTypeEnum(int code, String value, String englishValue, String portugueseValue) {
        this.code = code;
        this.value = value;
        this.englishValue = englishValue;
        this.portugueseValue = portugueseValue;
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

    public String getEnglishValue() {
        return englishValue;
    }

    public void setEnglishValue(String englishValue) {
        this.englishValue = englishValue;
    }

    public String getPortugueseValue() {
        return portugueseValue;
    }

    public void setPortugueseValue(String portugueseValue) {
        this.portugueseValue = portugueseValue;
    }

    public  static String getValueByLanguage(Integer code,SchoolLanguageEnum language) {
        if (code == null) {
            return "";
        }
        for (StudentLeaveTypeEnum ele : values()) {
            if (ele.getCode() == code)
            {
                switch (language) {
                    case ZH_MO:
                        return ele.getValue();
                    case EN_US:
                        return ele.getEnglishValue();
                    case PT_PT:
                        return ele.getPortugueseValue();
                }
            }
        }
        return "";
    }

    public static StudentLeaveTypeEnum toEnum(String value) {
        for (StudentLeaveTypeEnum ele : values()) {
            if (ele.getValue().equals(value) || 
                ele.getEnglishValue().equals(value) || 
                ele.getPortugueseValue().equals(value)) return ele;
        }
        return null;
    }

    public static StudentLeaveTypeEnum toEnum(Integer code) {
        for (StudentLeaveTypeEnum ele : values()) {
            if (ele.getCode() == code) return ele;
        }
        return null;
    }
}