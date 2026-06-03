package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("专业添加请求信息")
public class SchoolMajorAddReqModel {

    @ApiModelProperty("专业ID")
    private Long id;

//    @ApiModelProperty(value = "所属学年",required = false)
//    private String sid; // 修改: Long 改为 String

    @NotNull(message = "专业名称不能为空")
    @ApiModelProperty(value = "专业名称",required = true)
    private String majorName;

    @NotNull(message = "学部不能为空")
    @ApiModelProperty(value = "学部(1:幼稚园 2:小学 3:中学) 批量添加的学部必须一致",required = true)
    private Integer departmentId;

    @NotNull(message = "专业科目不能为空")
    @ApiModelProperty(value = "专业科目,隔开  这里传入的是原始科目表的id",required = true)
    private String majorSubjects;
}