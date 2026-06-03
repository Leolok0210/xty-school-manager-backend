package com.xiaotiyun.school.manager.basic.enums;

public enum ImportTaskTypeEnum {
    STUDENT_INFO(1, "学生资料"),
    STUDENT_IMAGE(2, "学生照片"),
    SUBJECT_INFO(3, "科目资料"),
    CLASS_INFO(4, "班级资料"),
    STUDENT_QUALITY_SCORE_INFO(5, "学生素质成绩"),
    STUDENT_ATTENDANCE(6, "学生考勤"),
    TEACHER_ATTENDANCE(7, "教师考勤"),
    USER_INFO(8, "用户信息"),
    VOLUNTEER(9, "义工服务"),
    ACTIVITY_STUDENT_REPORT(10, "活动匹配"),
    LEISURE_ACTIVITIES_SCORE(11, "余暇活动成绩"),
    ACTIVITY_COURSES(12, "余暇活动课程"),
    STUDENT_MEDICAL_RECORD(13, "学生医护保健记录"),
    STUDENT_REWARD(14, "学生奖励"),
    STUDENT_PUNISHMENT(15, "学生惩罚"),
    CONVENTIONAL_PERFORMANCE(16, "常规表现"),
    STUDENT_USUALLY_RULE(17, "平时成绩规则"),
    ;

    private int code;
    private String value;

    ImportTaskTypeEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static String getValue(int code) {
        for (ImportTaskTypeEnum ele : values()) {
            if (ele.getCode() == code) return ele.getValue();
        }
        return null;
    }
}
