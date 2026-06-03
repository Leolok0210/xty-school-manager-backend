package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

@Data
public class UserSchoolRelDTO {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 教师姓名
     */
    private String userName;
}
