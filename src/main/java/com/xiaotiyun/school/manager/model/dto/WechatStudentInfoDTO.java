package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class WechatStudentInfoDTO {
    /**
     * 学生UserID。学校内必须唯一。不区分大小写，长度为1~64个字节。只能由数字、字母和“_-@.”四种字符组成，且第一个字符必须是数字或字母。
     */
    private String student_userid;
    /**
     * 要变更的学生UserID,不能与已存在的UserID相同。每个学生仅能修改一次。
     */
    private String new_student_userid;
    /**
     * 学生姓名，长度为1~32个字符
     */
    private String name;
    /**
     * 学生手机号
     */
    private String mobile;
    /**
     * 学生所在的班级id列表,不超过20个
     */
    private List<Integer> department;
}
