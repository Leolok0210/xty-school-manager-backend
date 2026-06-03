package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public enum StudentVaccineStatusEnum {
    SUBMITTED_COMPLETED(1, "已提交，並已完成", "Submitted and Completed", "Submetido e Concluído"),
    SUBMITTED_INCOMPLETE(2, "已提交，但未完成接種", "Submitted but Incomplete", "Submetido mas Incompleto"),
    NOT_SUBMITTED(3, "未提交", "Not Submitted", "Não Submetido");

    @Getter
    private final int code;
    private final String zhTwValue;
    private final String enValue;
    private final String ptValue;

    StudentVaccineStatusEnum(int code, String zhTwValue, String enValue, String ptValue) {
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
        for (StudentVaccineStatusEnum studentVaccineStatusEnum : values()) {
            if (studentVaccineStatusEnum.code == code) {
                switch (language) {
                    case EN_US:
                        return studentVaccineStatusEnum.enValue;
                    case PT_PT:
                        return studentVaccineStatusEnum.ptValue;
                    default:
                        return studentVaccineStatusEnum.zhTwValue;
                }
            }
        }
        return "";
    }

    public static Integer getCode(String value, SchoolLanguageEnum language) {
        if (StringUtils.isNotBlank(value)) {
            for (StudentVaccineStatusEnum studentVaccineStatusEnum : values()) {
                switch (language) {
                    case EN_US:
                        if (studentVaccineStatusEnum.enValue.equals(value)) return studentVaccineStatusEnum.code;
                        break;
                    case PT_PT:
                        if (studentVaccineStatusEnum.ptValue.equals(value)) return studentVaccineStatusEnum.code;
                        break;
                    default:
                        if (studentVaccineStatusEnum.zhTwValue.equals(value)) return studentVaccineStatusEnum.code;
                }
            }
        }
        return null;
    }
}
