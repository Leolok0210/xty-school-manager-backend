package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "平时成绩权重配置响应类")
public class StudentUsuallyRuleResModel {

    @ApiModelProperty(value = "级组ID")
    private Long gradeGroupId;

    @ApiModelProperty(value = "级组名称")
    private String gradeGroupName;

    @ApiModelProperty(value = "科目ID")
    private Long subjectId;

    @ApiModelProperty(value = "科目名称")
    private String subjectName;

    @ApiModelProperty(value = "平时成绩类型ID")
    private Long typeId;

    @ApiModelProperty(value = "平时成绩类型名称")
    private String typeName;

    @ApiModelProperty(value = "权重单位%，结果*100")
    private Integer weight;

}