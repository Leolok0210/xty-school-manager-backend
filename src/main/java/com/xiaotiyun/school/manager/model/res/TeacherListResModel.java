package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TeacherListResModel {
    @ApiModelProperty("教师ID(学校关联表id)")
    private Long userId;
    @ApiModelProperty("用户名")
    private String userName;
    @ApiModelProperty("手机号")
    private String mobile;
}