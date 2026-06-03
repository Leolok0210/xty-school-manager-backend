package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户新增部门请求参数")
public class UserDeptAddReqModel {

    @ApiModelProperty("部门ID")
    private Long id;

    @ApiModelProperty("设为部门领导，1-是，0-否")
    private Integer isLeader;
}
