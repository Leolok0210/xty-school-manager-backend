package com.xiaotiyun.school.manager.basic.enums;

public enum SchoolLanguageEnum {
    ZH_MO("zh-MO", "繁体中文"),
    EN_US("en-US", "英文"),
    PT_PT("pt-PT", "葡萄牙语");

    private String code;
    private String value;

    SchoolLanguageEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static SchoolLanguageEnum getValue(String code) {
        for (SchoolLanguageEnum ele : values()) {
            if (ele.getCode().equals(code)) return ele;
        }
        return null;
    }
    public static SchoolLanguageEnum getDefValue(String code) {
        for (SchoolLanguageEnum ele : values()) {
            if (ele.getCode().equals(code)) return ele;
        }
        return ZH_MO;
    }
}
