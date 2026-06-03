package com.xiaotiyun.school.manager.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConditionTypeEnum {
    ALL("满足所有条件"),
    ANY("满足任一条件");
    
    private final String desc;
} 