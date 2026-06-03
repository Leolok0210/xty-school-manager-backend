package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class WechatMessageResDTO {
    // 错误码 0为成功
    private Integer errcode;
    // 错误信息
    private String errmsg;
    // 无效的家长userid列表
    private List<String> invalid_parent_userid;
    // 无效的学生userid列表
    private List<String> invalid_student_userid;
    // 无效的部门id列表
    private List<String> invalid_party;
}
