package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

public enum StudentPromotionTypeEnum {
    PROMOTION(1, "升级", "Promotion", "Promoção"),
    RETENTION(2, "留级", "Retention", "Repetição"),
    CONDITIONAL_PROMOTION(3, "带科", "Conditional Promotion", "Promoção Condicional");

    @Getter
    private final int code;
    private final String zhTwValue;
    private final String enValue;
    private final String ptValue;

    StudentPromotionTypeEnum(int code, String zhTwValue, String enValue, String ptValue) {
        this.code = code;
        this.zhTwValue = zhTwValue;
        this.enValue = enValue;
        this.ptValue = ptValue;
    }

    /**
     * 根据语言类型获取对应的枚举值
     *
     * @param code     枚举的code值
     * @param language 语言类型
     * @return 对应语言的枚举值
     */
    public static String getValue(int code, SchoolLanguageEnum language) {
        for (StudentPromotionTypeEnum promotionType : values()) {
            if (promotionType.code == code) {
                switch (language) {
                    case EN_US:
                        return promotionType.enValue;
                    case PT_PT:
                        return promotionType.ptValue;
                    default:
                        return promotionType.zhTwValue;
                }
            }
        }
        return "";
    }
}
