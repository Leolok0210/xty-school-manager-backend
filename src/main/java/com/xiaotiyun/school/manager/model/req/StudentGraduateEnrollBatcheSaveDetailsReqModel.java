package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StudentGraduateEnrollBatcheSaveDetailsReqModel {
    @NotNull(message = "学生id不能为空")
    @ApiModelProperty(value = "学生id", required = true)
    private Long studentId;
    @NotNull(message = "类型不能为空")
    @ApiModelProperty(value = "类型(1.留级；2.毕业)", required = true)
    private Integer type;
    @ApiModelProperty(value = "毕业类型(1.升学；2.就业)")
    private Integer graduateType;
    @ApiModelProperty(value = "就读地点")
    private String schoolAddress;
    @ApiModelProperty(value = "就读院校")
    private String faculty;
    @ApiModelProperty(value = "就读科系")
    private String department;
    @ApiModelProperty(value = "职业")
    private String job;
}