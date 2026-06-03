package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SchoolMajorQueryReqModel {
    @ApiModelProperty(value = "页码",required = true)
    private Integer pageNum;
    @ApiModelProperty(value = "每页大小",required = true)
    private Integer pageSize;
    @ApiModelProperty("专业名称")
    private String majorName;
    @ApiModelProperty(value = "学校ID",required = true)
    private Long schoolId;
    //科目名称
    @ApiModelProperty("科目名称")
    private String subjectName;
}