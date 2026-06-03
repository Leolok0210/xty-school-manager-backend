package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

@Data
public class EnterpriseWxChatUserResDTO {
    //student_userid
    private String student_userid;
    //errcode
    private Integer errcode;

    private String errmsg;
}
