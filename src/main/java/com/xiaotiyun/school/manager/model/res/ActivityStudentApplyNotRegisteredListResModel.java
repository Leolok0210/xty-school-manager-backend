package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ActivityStudentApplyNotRegisteredListResModel {
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
    @ApiModelProperty("是否录取")
    private Boolean isAdmitted;
    @ApiModelProperty("类型（1.预先导入 2.分配，3 一次报名志愿录入，4 二次报名志愿录入，5 二次报名系统随机分配，6 二次报名人工分配）")
    private Integer type;
} 