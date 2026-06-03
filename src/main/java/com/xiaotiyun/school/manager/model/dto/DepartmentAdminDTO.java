package com.xiaotiyun.school.manager.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "部门管理员信息")
public class DepartmentAdminDTO {
    @ApiModelProperty(value = "部门管理员的userid")
    private String userid;

    @ApiModelProperty(value = "部门管理员的类型，1表示校区负责人，2表示年级负责人，3表示班主任，4表示任课老师，5表示学段负责人")
    private Integer type;

    @ApiModelProperty(value = "教师或班主任的科目")
    private String subject;
}
