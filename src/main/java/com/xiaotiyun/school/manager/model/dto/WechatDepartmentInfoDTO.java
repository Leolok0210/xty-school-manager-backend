package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

@Data
public class WechatDepartmentInfoDTO {
    /**
     * 部门id，32位整型，指定时必须大于1。若不填该参数，将自动生成id
     */
    private Integer id;
    /**
     * 修改为新的id
     */
    private Integer new_id;
    /**
     * 父部门id，32位整型
     */
    private Integer parentid;
    /**
     * 部门名称。长度限制为1~32个字符，字符不能包括-:*?"<>/，*，当设置了入学年份和标准年级时，该参数将被忽略
     */
    private String name;
    /**
     * 部门类型，32位整型，1表示班级，2表示年级，3表示学段，4表示校区
     */
    private Integer type;
}
