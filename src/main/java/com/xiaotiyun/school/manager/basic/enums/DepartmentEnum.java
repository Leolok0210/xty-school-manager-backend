package com.xiaotiyun.school.manager.basic.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;

@Getter
@AllArgsConstructor
public enum DepartmentEnum {
    KINDERGARTEN(1, "幼稚园", "Kindergarten", "Creche"),
    PRIMARY(2, "小学", "Primary School", "Ensino Primário"),
    MIDDLE(3, "中学", "Middle School", "Ensino Secundário");

    private final Integer code;
    private final String desc;
    private final String descEn;
    private final String descPt;

    public static DepartmentEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (DepartmentEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    //getbydesc
    public static DepartmentEnum getByDesc(String desc) {
        if (desc == null) {
            return null;
        }
        for (DepartmentEnum value : values()) {
            if (value.getDesc().equals(desc)) {
                return value;
            }
        }
        return null;
    }
    
    public static String getDesc(Integer code, SchoolLanguageEnum currentLanguage) {
        for (DepartmentEnum value : DepartmentEnum.values()) {
            if (value.code.equals(code)) {
                switch (currentLanguage){
                    case EN_US:
                        return value.descEn;
                    case PT_PT:
                        return value.descPt;
                    default:
                        return value.desc;
                }
            }
        }
        return null;
    }
}