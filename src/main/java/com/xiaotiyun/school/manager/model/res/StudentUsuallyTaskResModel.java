package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel("平时分记录信息查询参数")
public class StudentUsuallyTaskResModel {
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "学校id")
    private Long schoolId;
    @ApiModelProperty(value = "次数")
    private Integer frequency;
    @ApiModelProperty(value = "平时成绩类型id")
    private Long typeId;
    @ApiModelProperty(value = "平时成绩类型名称")
    private String typeName;
    @ApiModelProperty(value = "测验名称")
    private String name;
    @ApiModelProperty(value = "测验时间")
    private LocalDate testDate;
    @ApiModelProperty(value = "学段id")
    private Long periodId;
    @ApiModelProperty(value = "班级id")
    private Long classId;
    @ApiModelProperty(value = "科目id")
    private Long subjectId;
    @ApiModelProperty(value = "备注")
    private String remark;
}