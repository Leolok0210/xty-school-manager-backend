package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public enum AdmissionInfoEnum {
    PRE_IMPORT(1, "未开始报名", "导入", "Ainda não começou a inscrição", "Importação", "Not Started Enrollment", "Import"),
    ASSIGN(2, "一次报名", "人工分配", "Inscrição única", "Distribuição manual", "First Enrollment", "Manual Assignment"),
    FIRST_ENROLLMENT(3, "一次报名", "报名抽签", "Inscrição única", "Sorteio de inscrição", "First Enrollment", "Enrollment Lottery"),
    SECOND_ENROLLMENT(4, "二次报名", "报名抽签", "Inscrição secundária", "Sorteio de inscrição", "Second Enrollment", "Enrollment Lottery"),
    SYSTEM_RANDOM(5, "二次报名", "随机分配", "Inscrição secundária", "Distribuição aleatória", "Second Enrollment", "Random Assignment"),
    SECOND_MANUAL(6, "二次报名", "人工分配", "Inscrição secundária", "Distribuição manual", "Second Enrollment", "Manual Assignment");

    @Getter
    private final int code;
    @Getter
    private final String zhTwStage;
    @Getter
    private final String zhTwMethod;
    @Getter
    private final String ptStage;
    @Getter
    private final String ptMethod;
    @Getter
    private final String enStage;
    @Getter
    private final String enMethod;

    AdmissionInfoEnum(int code, String zhTwStage, String zhTwMethod, String ptStage, String ptMethod, String enStage, String enMethod) {
        this.code = code;
        this.zhTwStage = zhTwStage;
        this.zhTwMethod = zhTwMethod;
        this.ptStage = ptStage;
        this.ptMethod = ptMethod;
        this.enStage = enStage;
        this.enMethod = enMethod;
    }

    /**
     * 根据类型获取录取阶段
     * @param code 类型代码
     * @param language 语言类型
     * @return 录取阶段文本
     */
    public static String getStage(int code, SchoolLanguageEnum language) {
        for (AdmissionInfoEnum admission : values()) {
            if (admission.code == code) {
                switch (language) {
                    case EN_US:
                        return admission.enStage;
                    case PT_PT:
                        return admission.ptStage;
                    default:
                        return admission.zhTwStage;
                }
            }
        }
        return "";
    }

    /**
     * 根据类型获取录取方式
     * @param code 类型代码
     * @param language 语言类型
     * @return 录取方式文本
     */
    public static String getMethod(int code, SchoolLanguageEnum language) {
        for (AdmissionInfoEnum admission : values()) {
            if (admission.code == code) {
                switch (language) {
                    case EN_US:
                        return admission.enMethod;
                    case PT_PT:
                        return admission.ptMethod;
                    default:
                        return admission.zhTwMethod;
                }
            }
        }
        return "";
    }

    /**
     * 根据录取阶段和录取方式获取类型代码
     * @param stage 录取阶段
     * @param method 录取方式
     * @param language 语言类型
     * @return 类型代码
     */
    public static Integer getCode(String stage, String method, SchoolLanguageEnum language) {
        if (StringUtils.isNotBlank(stage) && StringUtils.isNotBlank(method)) {
            for (AdmissionInfoEnum admission : values()) {
                switch (language) {
                    case EN_US:
                        if (admission.enStage.equals(stage) && admission.enMethod.equals(method)) 
                            return admission.code;
                        break;
                    case PT_PT:
                        if (admission.ptStage.equals(stage) && admission.ptMethod.equals(method)) 
                            return admission.code;
                        break;
                    default:
                        if (admission.zhTwStage.equals(stage) && admission.zhTwMethod.equals(method)) 
                            return admission.code;
                        break;
                }
            }
        }
        return null;
    }
}
