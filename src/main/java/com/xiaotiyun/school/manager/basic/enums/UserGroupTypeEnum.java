package com.xiaotiyun.school.manager.basic.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户组类型枚举
 */
@Getter
@AllArgsConstructor
public enum UserGroupTypeEnum {

    /**
     * 学校管理员
     */
    SCHOOL_ADMIN("school_admin", "学校管理员(系统)");

    /**
     * 编码
     */
    private final String code;

    /**
     * 名称
     */
    private final String name;

    /**
     * 根据code获取枚举
     */
    public static UserGroupTypeEnum getByCode(String code) {
        for (UserGroupTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 判断是否是学校管理员
     */
    public static boolean isSchoolAdmin(String code) {
        return SCHOOL_ADMIN.getCode().equals(code);
    }

} 