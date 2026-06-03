package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public enum UserRewardTypeEnum {
    // 惩罚类型
    GREAT_OFFENSE(1, "大過", "Major Demerit", "Falta Grave"),
    SMALL_OFFENSE(2, "小過", "Minor Demerit", "Falta Leve"),
    DEMERIT(3, "缺點", "Disciplinary Point", "Ponto Disciplinar"),
    // 奖励类型
    GREAT_REWARD(4, "大功", "Major Merit", "Grande Mérito"),
    SMALL_REWARD(5, "小功", "Minor Merit", "Pequeno Mérito"),
    MERIT(6, "優點", "Commendation", "Louvor");

    @Getter
    private final int code;
    @Getter
    private final String zhTwValue;
    @Getter
    private final String enValue;
    @Getter
    private final String ptValue;

    UserRewardTypeEnum(int code, String zhTwValue, String enValue, String ptValue) {
        this.code = code;
        this.zhTwValue = zhTwValue;
        this.enValue = enValue;
        this.ptValue = ptValue;
    }

    public static String getValue(Integer code, SchoolLanguageEnum language) {
        if (code != null) {
            for (UserRewardTypeEnum type : values()) {
                if (type.code == code) {
                    switch (language) {
                        case EN_US:
                            return type.enValue;
                        case PT_PT:
                            return type.ptValue;
                        default:
                            return type.zhTwValue;
                    }
                }
            }
        }
        return "";
    }

    public static Integer getCode(String value, SchoolLanguageEnum language) {
        if (StringUtils.isNotBlank(value)) {
            for (UserRewardTypeEnum type : values()) {
                switch (language) {
                    case EN_US:
                        if (type.enValue.equals(value)) return type.code;
                        break;
                    case PT_PT:
                        if (type.ptValue.equals(value)) return type.code;
                        break;
                    default:
                        if (type.zhTwValue.equals(value)) return type.code;
                }
            }
        }
        return null;
    }

    public static UserRewardTypeEnum getByValue(String value, SchoolLanguageEnum language) {
        if (StringUtils.isNotBlank(value)) {
            for (UserRewardTypeEnum type : values()) {
                switch (language) {
                    case EN_US:
                        if (type.enValue.equals(value)) return type;
                        break;
                    case PT_PT:
                        if (type.ptValue.equals(value)) return type;
                        break;
                    default:
                        if (type.zhTwValue.equals(value)) return type;
                }
            }
        }
        return null;
    }
}