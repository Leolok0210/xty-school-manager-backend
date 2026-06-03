package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任教设置详情返回信息")
public class TeachingSettingRoleResModel {
    @ApiModelProperty("用户主键ID")
    private Long id;

    @ApiModelProperty("任教老师ID")
    private Long teacherId;

    @ApiModelProperty("任教老师名称")
    private String teacherName;

    @ApiModelProperty("教师手机号")
    private String teacherPhoneNumber;
}