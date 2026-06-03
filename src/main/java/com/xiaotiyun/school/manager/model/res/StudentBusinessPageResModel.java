package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentBusinessPageResModel {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("学年")
    private String schoolYear;
    @ApiModelProperty("学生id")
    private Long studentId;
    @ApiModelProperty("学生姓名")
    private String studentName;
    @ApiModelProperty("级组id")
    private Long gradeId;
    @ApiModelProperty("级组名称")
    private String gradeName;
    @ApiModelProperty("班级id")
    private Long classId;
    @ApiModelProperty("班级名称")
    private String className;
    @ApiModelProperty("开始时间")
    private LocalDateTime startTime;
    @ApiModelProperty("结束时间")
    private LocalDateTime endTime;
    @ApiModelProperty("公务事由")
    private String reason;
}