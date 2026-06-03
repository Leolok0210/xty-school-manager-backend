package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ConventionalPerformancePageResModel {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("学年")
    private String sid;
    @ApiModelProperty("学期id")
    private Long term;
    @ApiModelProperty("学段name")
    private String termName;
    @ApiModelProperty("组级name")
    public String gradeGroupName;
    @ApiModelProperty("班级ID")
    private Long classId;
    @ApiModelProperty("班级名称")
    private String className;
    @ApiModelProperty("学生ID")
    private Long studentId;
    @ApiModelProperty("学生姓名")
    private String studentName;
    @ApiModelProperty("事件日期")
    private LocalDate date;
    @ApiModelProperty("类型(1.上课违规;2.欠作业;3.仪表不符;5.欠课本;7.欠回条)")
    private Integer type;
    @ApiModelProperty("次数")
    private Integer frequency;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("创建人ID")
    private Long createId;
    @ApiModelProperty("创建人姓名")
    private String createName;
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}