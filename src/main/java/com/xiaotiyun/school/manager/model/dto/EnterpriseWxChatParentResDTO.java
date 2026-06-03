package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

@Data
public class EnterpriseWxChatParentResDTO {
    //student_userid
    private String parent_userid;
    //errcode
    private Integer errcode;

    private String errmsg;
}
