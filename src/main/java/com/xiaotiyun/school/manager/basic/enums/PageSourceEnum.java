package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 页面来源枚举
 * 
 * @author system
 */
@Getter
public enum PageSourceEnum {
    PRE_IMPORT(1, "预先导入", "Pre-import", "Pré-importação"),
    MATCHED(2, "已匹配", "Matched", "Correspondido"),
    NO_COURSE(3, "无课程", "No Course", "Sem Curso"),
    SECOND_APPLY(4, "二次报名", "Second Application", "Segunda Inscrição");

    private final int code;
    private final String zhTwValue;
    private final String enValue;
    private final String ptValue;

    PageSourceEnum(int code, String zhTwValue, String enValue, String ptValue) {
        this.code = code;
        this.zhTwValue = zhTwValue;
        this.enValue = enValue;
        this.ptValue = ptValue;
    }

    /**
     * 根据代码和语言获取对应的值
     * 
     * @param code 代码
     * @param language 语言
     * @return 对应的值
     */
    public static String getValue(int code, SchoolLanguageEnum language) {
        for (PageSourceEnum item : values()) {
            if (item.code == code) {
                switch (language) {
                    case EN_US:
                        return item.enValue;
                    case PT_PT:
                        return item.ptValue;
                    default:
                        return item.zhTwValue;
                }
            }
        }
        return "";
    }

    /**
     * 根据值和语言获取对应的代码
     * 
     * @param value 值
     * @param language 语言
     * @return 对应的代码
     */
    public static Integer getCode(String value, SchoolLanguageEnum language) {
        if (StringUtils.isNotBlank(value)) {
            for (PageSourceEnum item : values()) {
                switch (language) {
                    case EN_US:
                        if (item.enValue.equals(value)) return item.code;
                        break;
                    case PT_PT:
                        if (item.ptValue.equals(value)) return item.code;
                        break;
                    default:
                        if (item.zhTwValue.equals(value)) return item.code;
                }
            }
        }
        return null;
    }

    /**
     * 根据代码获取枚举
     * 
     * @param code 代码
     * @return 枚举
     */
    public static PageSourceEnum getByCode(int code) {
        for (PageSourceEnum item : values()) {
            if (item.code == code) {
                return item;
            }
        }
        return null;
    }
} 