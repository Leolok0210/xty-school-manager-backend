package com.xiaotiyun.school.manager.basic.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DepartmentLanguageEnum {
    KINDERGARTEN(1, "幼稚園", "Kindergarten", "Creche"),
    PRIMARY(2, "小學", "Primary School", "Ensino Primário"),
    MIDDLE(3, "中學", "Middle School", "Ensino Secundário");


    private final Integer code;
    private final String desc;
    private final String descEn;
    private final String descPt;
    
    public static String getByCode(Integer code,SchoolLanguageEnum schoolLanguage) {
        if (code == null) {
            return null;
        }
        for (DepartmentLanguageEnum value : values()) {
            if (value.getCode().equals(code)) {
                switch (schoolLanguage) {
                    case ZH_MO:
                        return value.desc;
                    case EN_US:
                        return value.descEn;
                    case PT_PT:
                        return value.descPt;
                    default:
                        return null;
                }
            }
        }
        return null;
    }

    //getbydesc
    public static DepartmentLanguageEnum getByDesc(String desc) {
        if (desc == null) {
            return null;
        }
        for (DepartmentLanguageEnum value : values()) {
            if (value.getDesc().equals(desc)) {
                return value;
            }
        }
        return null;
    }
} 