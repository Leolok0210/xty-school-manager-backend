package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

/**
 * 数据业务类型枚举
 */
@Getter
public enum DataBusinessTypeEnum {
    /**
     * 平时成绩登记
     */
    DAILY_GRADE(1, "平时成绩登记"),

    /**
     * 考试成绩登记
     */
    EXAM_GRADE(2, "考试成绩登记"),

    /**
     * 毕业考试登记
     */
    GRADUATION_EXAM(3, "毕业考试登记"),

    /**
     * 学年素质登记
     */
    ANNUAL_QUALITY(4, "学年素质登记"),

    /**
     * 奖励登记
     */
    REWARD(5, "奖励/惩罚登记"),

    /**
     * 课堂表现登记
     */
    CLASS_BEHAVIOR(7, "课堂表现登记"),

    /**
     * 欠交作业登记
     */
    MISSING_HOMEWORK(8, "欠交作业登记"),

    /**
     * 仪表不符登记
     */
    APPEARANCE_VIOLATION(9, "仪表不符登记"),

    /**
     * 巡堂登记
     */
    PATROL_RECORD(10, "巡堂登记"),

    /**
     * 大息小息表现登记
     */
    BREAK_BEHAVIOR(11, "大息小息表现登记"),

    /**
     * 课外比赛登记
     */
    EXTRA_CURRICULAR_COMPETITION(12, "课外比赛登记"),

    /**
     * 校外比赛登记
     */
    OUTSIDE_COMPETITION(13, "校外比赛登记"),

    /**
     * 义工服务
     */
    VOLUNTEER_SERVICE(14, "义工服务"),

    /**
     * 平时成绩任务
     */
    USUALLY_TASK(15, "平时成绩任务"),

    /**
     * 考试成绩任务
     */
    EXAM_TASK(16, "考试成绩任务"),

    /**
     * 毕业成绩任务
     */
    GRADUATE_EXAM_TASK(17, "毕业成绩任务");

    private final Integer value;
    private final String description;

    DataBusinessTypeEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 根据值获取枚举实例
     *
     * @param value 枚举值
     * @return 对应的枚举对象，若未找到则返回 null
     */
    public static DataBusinessTypeEnum fromValue(Integer value) {
        for (DataBusinessTypeEnum type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
}
