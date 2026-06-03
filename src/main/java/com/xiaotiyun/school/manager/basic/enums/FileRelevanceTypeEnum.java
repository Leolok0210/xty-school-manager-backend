package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

public enum FileRelevanceTypeEnum {
    STUDENT_LEAVE(5, "学生请假照片"),
    TEACHER_LEAVE(6, "教师请假照片"),
    TEACHER_BUSINESS(7, "教师公务照片"),
    ENTERPRISE_WECHAT_NOTICE(8, "企业微信通知照片");

    @Getter
    private final Integer type;
    @Getter
    private final String desc;

    FileRelevanceTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
