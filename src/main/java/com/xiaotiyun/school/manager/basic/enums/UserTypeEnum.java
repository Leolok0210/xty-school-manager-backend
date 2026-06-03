package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

/**
 * 用户类型枚举
 */
@Getter
public enum UserTypeEnum {
    
    NORMAL_USER(1, "普通用户"),
    SUPER_ADMIN(2, "超级管理员"),
    SCHOOL_ADMIN(3, "学校管理员");

    private final Integer code;
    private final String desc;

    UserTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static UserTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (UserTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    // 判断是否是超级管理员
    public static boolean isSuperAdmin(Integer code) {
        return getByCode(code) == SUPER_ADMIN;
    }

    // 判断是否是普通用户
    public static boolean isNormalUser(Integer code) {
        return getByCode(code) == NORMAL_USER;
    }
} 