package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户部门信息返回参数")
public class UserDeptDetailResModel {

    @ApiModelProperty("部门ID")
    private Long id;

    @ApiModelProperty("部门名称")
    private String name;

    @ApiModelProperty("是否为部门领导，1-是，0-否")
    private Integer isLeader;
}
