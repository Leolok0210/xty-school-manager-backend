package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 请假类型枚举
 */
public enum LeaveTypeEnum {
    PERSONAL_LEAVE(1, "事假"),
    SICK_LEAVE(2, "病假"),
    ANNUAL_LEAVE(3, "年假"),
    MATERNITY_LEAVE(4, "产假"),
    PATERNITY_LEAVE(5, "陪产假"),
    MARRIAGE_LEAVE(6, "婚假"),
    FUNERAL_LEAVE(7, "丧假"),
    PRENATAL_LEAVE(8, "产检假"),
    PARENTING_LEAVE(9, "育儿假");

    @Getter
    private final Integer code;
    @Getter
    private final String desc;

    LeaveTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据编码获取枚举
     */
    public static LeaveTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (LeaveTypeEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 根据名称获取枚举
     */
    public static LeaveTypeEnum getByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        try {
            return LeaveTypeEnum.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
