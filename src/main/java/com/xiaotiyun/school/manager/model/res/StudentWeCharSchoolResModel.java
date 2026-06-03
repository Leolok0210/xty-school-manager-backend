package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "切换学校列表返回对象")
public class StudentWeCharSchoolResModel {
    @ApiModelProperty(value = "学校ID", example = "1")
    private Long schoolId;

    @ApiModelProperty(value = "学校名称", example = "2023-2024")
    private String schoolName;

    @ApiModelProperty(value = "学生ID", example = "true")
    private Long studentId;

    @ApiModelProperty(value = "学生名称", example = "true")
    private String studentName;
}