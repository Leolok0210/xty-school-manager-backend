package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SysClassListResModel {
    @ApiModelProperty("班级ID")
    private Long classId;
    @ApiModelProperty("班级名称")
    private String className;
    @ApiModelProperty("数值(不返回，字段后台使用)")
    private String groupName;
    @ApiModelProperty("级组(系统设置级组名称)")
    private String grade;
    @ApiModelProperty("班级组级ID")
    private Integer groupId;
    //学部
    @ApiModelProperty("学部")
    private Integer department;
}