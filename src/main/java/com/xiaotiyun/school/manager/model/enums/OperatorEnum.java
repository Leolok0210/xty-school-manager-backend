package com.xiaotiyun.school.manager.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperatorEnum {
    GT(">", "大于"),
    GTE(">=", "大于等于"),
    LT("<", "小于"),
    LTE("<=", "小于等于");
    
    private final String symbol;
    private final String desc;
} 