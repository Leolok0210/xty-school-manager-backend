package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

/**
 * 数据操作类型枚举
 */
@Getter
public enum DataOperationTypeEnum {
    /**
     * 新增操作
     */
    CREATE(1, "新增"),

    /**
     * 修改操作
     */
    UPDATE(2, "修改");

    private final Integer value;
    private final String description;

    DataOperationTypeEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 根据值获取枚举实例
     *
     * @param value 枚举值
     * @return 对应的枚举对象，若未找到则返回 null
     */
    public static DataOperationTypeEnum fromValue(Integer value) {
        for (DataOperationTypeEnum type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
}
