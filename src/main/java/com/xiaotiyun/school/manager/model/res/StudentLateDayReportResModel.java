package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "学生迟到统计-天-返回对象")
public class StudentLateDayReportResModel {
    @ApiModelProperty(value = "班级ID", example = "1")
    private String classId;

    @ApiModelProperty(value = "班级名称", example = "高一(1)班")
    private String classname;

    @ApiModelProperty(value = "级组名称", example = "三年级")
    private String gradeGroupName;

    @ApiModelProperty(value = "迟到次数", example = "1")
    private String lateCount;

    @ApiModelProperty(value = "学生列表", example = "[]")
    private List<StudentLateDayStudentReportResModel> detail;
}
