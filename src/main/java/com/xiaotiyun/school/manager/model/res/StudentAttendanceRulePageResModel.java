package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
@ApiModel("学生出勤规则响应列表")
public class StudentAttendanceRulePageResModel {
    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty("学校id")
    private Long schoolId;
    @ApiModelProperty("学年")
    private String schoolYear;
    @ApiModelProperty("规则名称")
    private String ruleName;
    @ApiModelProperty("适用级组")
    private List<StudentAttendanceRuleGradePageResModel> grades;
    @ApiModelProperty("上午入校时间")
    private LocalTime morningInTime;
    @ApiModelProperty("下午离校时间")
    private LocalTime afternoonOutTime;
}