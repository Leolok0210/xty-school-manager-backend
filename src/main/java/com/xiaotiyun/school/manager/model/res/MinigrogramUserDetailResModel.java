package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "小程序登入返回用户详情对象")
@Data
public class MinigrogramUserDetailResModel {
    @ApiModelProperty("级组ID")
    private Long gradeGroupId;

    @ApiModelProperty("级组名称")
    private String gradeGroupName;

    @ApiModelProperty("班级ID")
    private Long classId;

    @ApiModelProperty("班级名称")
    private String className;

    @ApiModelProperty("班内号")
    private Integer classInnerNo;
}
