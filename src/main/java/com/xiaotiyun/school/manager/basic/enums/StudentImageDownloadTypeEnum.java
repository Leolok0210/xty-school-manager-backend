package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

@Getter
public enum StudentImageDownloadTypeEnum {
    STUDENT_NUMBER(1, "班内号_学生编号"),
    EDUCATION_BUREAU_NUMBER(2, "班级号_教青局编号"),
    CLASS_NAME_STUDENT_NUMBER_CHINESE_NAME(3, "班级名称_班内号_中文姓名"),
    SYSTEM_PHOTO_NAME(4, "系统内相片名称");

    private final int code;
    private final String name;

    StudentImageDownloadTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static StudentImageDownloadTypeEnum toEnum(int code) {
        for (StudentImageDownloadTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
} 