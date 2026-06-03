package com.xiaotiyun.school.manager.basic.enums;

/**
 * 余暇活动匹配类型枚举
 */
public enum LeisureActivityMatchTypeEnum {
    PRE_IMPORT(1, "预先导入"),
    ASSIGN(2, "分配"),
    FIRST_APPLY_VOLUNTEER(3, "一次报名志愿录入"),
    SECOND_APPLY_VOLUNTEER(4, "二次报名志愿录入"),
    SECOND_RANDOM_ALLOCATION(5, "二次报名系统随机分配"),
    SECOND_ASSIGN(6, "二次报名人工分配");

    private Integer code;
    private String value;

    LeisureActivityMatchTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 根据code获取枚举
     */
    public static LeisureActivityMatchTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (LeisureActivityMatchTypeEnum ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele;
            }
        }
        return null;
    }

    /**
     * 根据code获取value
     */
    public static String getValueByCode(Integer code) {
        LeisureActivityMatchTypeEnum enumValue = getByCode(code);
        return enumValue != null ? enumValue.getValue() : null;
    }

    /**
     * 判断code是否有效
     */
    public static boolean isValidCode(Integer code) {
        return getByCode(code) != null;
    }

    /**
     * 判断是否为志愿录入类型
     */
    public static boolean isVolunteerType(Integer code) {
        return code != null && (code == 3 || code == 4);
    }

    /**
     * 判断是否为一次报名志愿录入
     */
    public static boolean isFirstApplyVolunteer(Integer code) {
        return code != null && code == 3;
    }

    /**
     * 判断是否为二次报名志愿录入
     */
    public static boolean isSecondApplyVolunteer(Integer code) {
        return code != null && code == 4;
    }
} 