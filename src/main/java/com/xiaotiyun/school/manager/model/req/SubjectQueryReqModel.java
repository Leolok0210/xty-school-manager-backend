package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("科目查询请求信息")
public class SubjectQueryReqModel {

    @ApiModelProperty("科目编号")
    private String subjectNumber;

    @ApiModelProperty("科目名称")
    private String subjectName;

    @NotNull(message = "学校id不能为空")
    @ApiModelProperty(value = "学校id",required = true)
    private Long schoolId;


    @ApiModelProperty(value = "当前页码",required = true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页大小",required = true)
    private Integer pageSize;
}