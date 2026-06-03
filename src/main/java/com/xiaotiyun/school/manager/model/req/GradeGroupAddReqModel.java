package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@ApiModel("级组添加请求信息")
public class GradeGroupAddReqModel {
    @ApiModelProperty("级组ID")
    private Long id;

    @NotNull(message = "学部不能为空")
    @ApiModelProperty(value = "学部（1:幼稚园 2:小学 3:中学）",required = true)
    private Long department;

    @Size(max = 20, message = "级组名称不能超过20个字符")
    @NotNull(message = "级组名称不能为空")
    @ApiModelProperty(value = "级组名称",required = true)
    private String gradeGroupName;

    @NotNull(message = "学校ID不能为空")
    @ApiModelProperty(value = "学校ID",required = true)
    private Long schoolId;

    @NotNull(message = "级组不能为空")
    @ApiModelProperty(value = "级组",required = true)
    private String grade;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "文科理科：0-不分科；1-专业分科；2-文理分科 3-理工商分科",required = true)
    private Integer professionalSubject;

    @ApiModelProperty(value = "文理分科类型：1-按班级分科；2-按学生分科。当文理分科或理工商分科时，该字段必填")
    private Integer artsScienceType;
}