package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class SysClassPromoteReqModel {

    @ApiModelProperty(value = "密码", required = true)
    private String password;

    @ApiModelProperty(value = "user表的用户id", required = true)
    private Long userId;
}