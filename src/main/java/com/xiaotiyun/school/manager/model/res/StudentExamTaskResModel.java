package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StudentExamTaskResModel {
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "学校id")
    private Long schoolId;
    @ApiModelProperty(value = "考试名称")
    private String name;
    @ApiModelProperty(value = "学段id")
    private Long periodId;
    @ApiModelProperty(value = "班级id")
    private Long classId;
    @ApiModelProperty(value = "科目id")
    private Long subjectId;
    @ApiModelProperty(value = "备注")
    private String remark;
}