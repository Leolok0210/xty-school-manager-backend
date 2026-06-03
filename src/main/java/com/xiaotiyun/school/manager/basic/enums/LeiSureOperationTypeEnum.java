package com.xiaotiyun.school.manager.basic.enums;

/**
 * 余暇活动操作类型枚举
 */
public enum LeiSureOperationTypeEnum {
    BATCH_IMPORT(0, "批量导入"),
    REMOVE(1, "移除"),
    BATCH_REMOVE(2, "批量移除"),
    ASSIGN(3, "分配"),
    BATCH_ASSIGN(4, "批量分配"),
    TRANSFER_CLASS(5, "转班"),
    BATCH_TRANSFER_CLASS(6, "批量转班"),
    TRANSFER_IN(7, "转入"),
    BATCH_TRANSFER_IN(8, "批量转入");

    private Integer code;
    private String value;

    LeiSureOperationTypeEnum(Integer code, String value) {
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
    public static LeiSureOperationTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (LeiSureOperationTypeEnum ele : values()) {
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
        LeiSureOperationTypeEnum enumValue = getByCode(code);
        return enumValue != null ? enumValue.getValue() : null;
    }

    /**
     * 判断code是否有效
     */
    public static boolean isValidCode(Integer code) {
        return getByCode(code) != null;
    }
}
