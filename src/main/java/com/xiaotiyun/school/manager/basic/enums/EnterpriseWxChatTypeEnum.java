package com.xiaotiyun.school.manager.basic.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnterpriseWxChatTypeEnum {

    //关联类型 1-级组 2-班级 3-学生 4-家长 5-学部
    RELEVANCE_TYPE_LEVEL_GROUP(1, "级组"),
    RELEVANCE_TYPE_CLASS(2, "班级"),
    RELEVANCE_TYPE_STUDENT(3, "学生"),
    RELEVANCE_TYPE_PARENT(4, "家长"),
    RELEVANCE_TYPE_SECTION(5, "学部"),
    ;
    private Integer code;

    private String message;
    public static EnterpriseWxChatTypeEnum getMessageByCode(Integer code) {
        for (EnterpriseWxChatTypeEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
