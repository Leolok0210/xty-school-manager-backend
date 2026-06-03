package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SubstitutePageResModel {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty(value = "代课日期")
    private LocalDate substituteDate;
    @ApiModelProperty(value = "代课类型(1-7;1=周一，7=周日)")
    private Integer substituteType;
    @ApiModelProperty(value = "课节id")
    private Long lessonId;
    @ApiModelProperty(value = "课节名称")
    private String lessonName;
    @ApiModelProperty(value = "级组id")
    private Long gradeId;
    @ApiModelProperty(value = "级组名称")
    private String gradeName;
    @ApiModelProperty(value = "班级id")
    private Long classId;
    @ApiModelProperty(value = "班级名称")
    private String className;
    @ApiModelProperty(value = "科目id")
    private Long subjectId;
    @ApiModelProperty(value = "科目名称")
    private String subjectName;
    @ApiModelProperty(value = "原任课老师ID")
    private Long originalTeacherId;
    @ApiModelProperty(value = "原任课老师名称")
    private String originalTeacherName;
    @ApiModelProperty(value = "代课老师ID")
    private Long substituteTeacherId;
    @ApiModelProperty(value = "代课老师名称")
    private String substituteTeacherName;
    @ApiModelProperty(value = "备注")
    private String remark;
}