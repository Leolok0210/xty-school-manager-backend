package com.xiaotiyun.school.manager.basic.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnterpriseWeChatCacheTypeEnum {

    S_TICKET_TEA("SuiteTicket-Teacher", "教师端SuiteTicket"),
    TOKEN_TEA("Token-Teacher", "教师端Token"),
    ENT_TOKEN("Token-Enterprise-", "企业Token头，后面需要加上学校id"),

    S_TICKET_STU("SuiteTicket-Student", "学生端SuiteTicket"),
    TOKEN_STU("Token-Student", "学生端Token"),
    ;

    private final String code;
    private final String desc;
}
