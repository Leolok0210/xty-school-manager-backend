package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class StudentExamTaskPageResModel {
    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty("考试名称")
    private String name;
    @ApiModelProperty("班级id")
    private Long classId;
    @ApiModelProperty("班级名称")
    private String className;
    @ApiModelProperty("级组名称")
    private String gradeGroupName;
    @ApiModelProperty("学年")
    private String schoolYear;
    @ApiModelProperty("学段名称")
    private String periodName;
    @ApiModelProperty("科目名称")
    private String subjectName;
    @ApiModelProperty("参与人数")
    private Integer partakeCount;
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty("更新人")
    private String updateUser;
    @ApiModelProperty("备注")
    private String remark;
}