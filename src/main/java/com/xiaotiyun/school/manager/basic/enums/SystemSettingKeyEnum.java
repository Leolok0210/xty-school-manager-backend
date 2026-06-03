package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

@Getter
public enum SystemSettingKeyEnum {
    LOGO("logo", "网站logo图片地址"),
    DATE_FORMAT("dateFormat", "日期格式"),
    TIME_FORMAT("timeFormat", "时间格式"),
    LANGUAGE("language", "默认语言"),
    DEPARTMENTS("departments", "学部设置"),
    EVALUATION_COMMENT("evaluationComment", "默认素质评语"),
    SCHOOL_DEPARTMENTS("schoolDepartments", "校部设置"),
    PENALTY_RULES("penaltyRules", "惩罚规则设置"),
    LEISURE_ACTIVITIES_RATING("leisureActivitiesRating", "余暇活动评级规则设置"),
    UNCONVENTIONAL_PERFORMANCE("unconventionalPerformance", "非常规表现设定"),
    USUAL_TYPE_REL_SUB("usualTypeRelSub", "平时成绩类型是否关联科目");
    ;

    private final String key;
    private final String name;

    SystemSettingKeyEnum(String key, String name) {
        this.key = key;
        this.name = name;
    }

}
