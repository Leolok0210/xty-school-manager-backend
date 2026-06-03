package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public enum ApplyStageEnum {
    FIRST_ENROLLMENT(1, "一次报名", "Inscrição única", "First Enrollment"),
    SECOND_ENROLLMENT(2, "二次报名", "Inscrição secundária", "Second Enrollment");

    @Getter
    private final int code;
    @Getter
    private final String zhTwValue;
    @Getter
    private final String ptValue;
    @Getter
    private final String enValue;

    ApplyStageEnum(int code, String zhTwValue, String ptValue, String enValue) {
        this.code = code;
        this.zhTwValue = zhTwValue;
        this.ptValue = ptValue;
        this.enValue = enValue;
    }

    /**
     * 根据类型获取报名阶段
     * @param code 类型代码
     * @param language 语言类型
     * @return 报名阶段文本
     */
    public static String getValue(int code, SchoolLanguageEnum language) {
        for (ApplyStageEnum stage : values()) {
            if (stage.code == code) {
                switch (language) {
                    case EN_US:
                        return stage.enValue;
                    case PT_PT:
                        return stage.ptValue;
                    default:
                        return stage.zhTwValue;
                }
            }
        }
        return "";
    }

    /**
     * 根据报名阶段文本获取类型代码
     * @param value 报名阶段文本
     * @param language 语言类型
     * @return 类型代码
     */
    public static Integer getCode(String value, SchoolLanguageEnum language) {
        if (StringUtils.isNotBlank(value)) {
            for (ApplyStageEnum stage : values()) {
                switch (language) {
                    case EN_US:
                        if (stage.enValue.equals(value)) return stage.code;
                        break;
                    case PT_PT:
                        if (stage.ptValue.equals(value)) return stage.code;
                        break;
                    default:
                        if (stage.zhTwValue.equals(value)) return stage.code;
                        break;
                }
            }
        }
        return null;
    }
}
