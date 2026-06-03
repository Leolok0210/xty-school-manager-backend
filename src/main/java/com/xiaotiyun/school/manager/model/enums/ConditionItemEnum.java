package com.xiaotiyun.school.manager.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConditionItemEnum {
    CONDUCT("操行"),
    MAJOR_MERIT("大功"),
    MINOR_MERIT("小功"),
    MERIT_POINT("优点"),
    TOTAL_MERIT("总功劳"),
    MAJOR_DEMERIT("大过"),
    MINOR_DEMERIT("小过"),
    DEMERIT_POINT("缺点"),
    TOTAL_DEMERIT("总惩罚"),
    LEAVE("请假"),
    LATE("迟到"),
    ABSENT("缺席");
    
    private final String desc;
} 