package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActivityStudentApplyRegisteredListResModel {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("学生id")
    private Long studentId;
    @ApiModelProperty("级组名称")
    private String gradeGroupName;
    @ApiModelProperty("班级名称")
    private String className;
    @ApiModelProperty("班内号")
    private Integer seatNo;
    @ApiModelProperty("学生姓名")
    private String studentName;
    @ApiModelProperty("学生编号")
    private String studentNo;
    @ApiModelProperty("类型（1.一次报名 2.二次报名）")
    private Integer type;
    @ApiModelProperty("报名时间")
    private LocalDateTime createTime;
    @ApiModelProperty("志愿信息")
    private List<ActivityStudentApplyRegisteredVolunteerResModel> volunteers;
} 