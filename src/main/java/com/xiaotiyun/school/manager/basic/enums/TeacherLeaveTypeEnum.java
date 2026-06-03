package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

@Getter
public enum TeacherLeaveTypeEnum {
    PERSONAL_LEAVE(1, "事假"),
    SICK_LEAVE(2, "病假"),
    ANNUAL_LEAVE(3, "年假"),
    MATERNITY_LEAVE(4, "产假"),
    PATERNITY_LEAVE(5, "陪产假"),
    MARRIAGE_LEAVE(6, "婚假"),
    BEREAVEMENT_LEAVE(7, "丧假"),
    PRENATAL_LEAVE(8, "产检假"),
    PARENTING_LEAVE(9, "育儿假");

    private final int code;
    private final String name;

    TeacherLeaveTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(int code) {
        for (TeacherLeaveTypeEnum type : values()) {
            if (type.code == code) {
                return type.name;
            }
        }
        return "";
    }

    //toenum by code
    public static TeacherLeaveTypeEnum toEnum(int code) {
        for (TeacherLeaveTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
} 