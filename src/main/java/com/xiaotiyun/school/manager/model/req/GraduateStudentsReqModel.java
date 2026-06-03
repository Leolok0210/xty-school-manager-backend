package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GraduateStudentsReqModel {
    @ApiModelProperty(value = "user表的用户id", required = true)
    private Long userId;
    @ApiModelProperty(value = "班级id", required = true)
    private List<Long> classIds;
    @ApiModelProperty(value = "密码", required = true)
    private String password;

}