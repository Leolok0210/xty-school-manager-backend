package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

@Getter
public enum DepartmentScoreRuleEnum {
    //成绩类型,0-平时成绩,1-考试成绩,2-科目成绩 3-公共+文科/公共+理工科 4-公共+理科 5平均分规则 6-公共+商科
    NORMAL_SCORE("0"),

    EXAM_SCORE("1"),

    SUBJECT_SCORE("2"),

    COMMON_SCHOOL_SCORE("3"),

    COMMON_PROVINCE_SCORE("4"),

    SCORE_AVG_RULE("5"),

    COMMON_COMMERCE_SCORE("6");
    private final String value;
    DepartmentScoreRuleEnum(String value) {
        this.value = value;
    }

    public static DepartmentScoreRuleEnum getByValue(String value) {
        for (DepartmentScoreRuleEnum item : DepartmentScoreRuleEnum.values()) {
            if (item.getValue().equals(value)) {
                return item;
            }
        }
        return null;
    }
    //是否是2，3，4,6中的一个
    public static boolean isSubjectScore(String value) {
        return SUBJECT_SCORE.value.equals(value) || COMMON_SCHOOL_SCORE.value.equals(value) ||
                COMMON_PROVINCE_SCORE.value.equals(value) || COMMON_COMMERCE_SCORE.value.equals(value);
    }
}
