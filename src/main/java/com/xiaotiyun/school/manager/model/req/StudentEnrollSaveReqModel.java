package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StudentEnrollSaveReqModel {
    @ApiModelProperty(value = "学校id", required = true)
    @NotNull(message = "学校id不能为空")
    private Long schoolId;
    @ApiModelProperty(value = "学生id", required = true)
    @NotNull(message = "学生id不能为空")
    private Long studentId;
    @ApiModelProperty(value = "原校名称")
    private String schoolName;
    @ApiModelProperty(value = "原校学历")
    private String qualification;
    @ApiModelProperty(value = "班别")
    private String className;
    @ApiModelProperty(value = "学年")
    private String schoolYear;
    @ApiModelProperty(value = "学期(1.升级;2.留级;3.中途退学)")
    private Integer type;
    @ApiModelProperty(value = "语文成绩")
    private String chineseScore;
    @ApiModelProperty(value = "英文成绩")
    private String englishScore;
    @ApiModelProperty(value = "数学成绩")
    private String mathsScore;
    @ApiModelProperty(value = "平均分")
    private String averageScore;
    @ApiModelProperty(value = "评语")
    private String comment;
    @ApiModelProperty(value = "操行")
    private String behavior;
    @ApiModelProperty(value = "奖惩")
    private String bonusPenalty;
    @ApiModelProperty(value = "报考年级")
    private String gradeApplication;
    @ApiModelProperty(value = "录取年级")
    private String admissionGrade;
    @ApiModelProperty(value = "介绍人")
    private String referrer;
    @ApiModelProperty(value = "入学语文成绩")
    private String enrollChineseScore;
    @ApiModelProperty(value = "入学英文成绩")
    private String enrollEnglishScore;
    @ApiModelProperty(value = "入学数学成绩")
    private String enrollMathsScore;
}