package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class StudentScoreReqModel {
    @NotNull(message = "学校id不能为空")
    @ApiModelProperty(value = "学校id", required = true)
    private Long schoolId;
    @NotNull(message = "学生id不能为空")
    @ApiModelProperty(value = "学生id", required = true)
    private Long studentId;
    @NotNull(message = "成绩类型不能为空")
    @ApiModelProperty(value = "成绩类型(1.平时成绩；2.考试成绩；3.毕业成绩)", required = true)
    private Integer type;
    @NotBlank(message = "学年不能为空")
    @ApiModelProperty(value = "学年", required = true)
    private String schoolYear;
    @ApiModelProperty(value = "学段id")
    private Long periodId;
    @ApiModelProperty(value = "班级id")
    private Long classId;
    @ApiModelProperty(value = "科目id")
    private Long subjectId;
}
