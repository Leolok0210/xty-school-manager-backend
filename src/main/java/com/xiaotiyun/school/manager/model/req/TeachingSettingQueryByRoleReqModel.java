package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任教设置查询请求信息根据角色")
public class TeachingSettingQueryByRoleReqModel {
    @ApiModelProperty(value = "学校ID",required = true)
    private Long schoolId;

    @ApiModelProperty("老师ID")
    private Long teacherId;

    @ApiModelProperty("老师名称")
    private String teacherName;

    @ApiModelProperty(value = "页码",required = true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页大小",required = true)
    private Integer pageSize;
}