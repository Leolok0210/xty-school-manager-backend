package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 义工服务响应模型
 */
@Data
@ApiModel("义工服务响应详情")
public class VolunteerResModel {

    @ApiModelProperty("记录ID")
    private Long id;

    @ApiModelProperty("学校ID")
    private Long schoolId;

    @ApiModelProperty("学年")
    private String schoolYear;

    @ApiModelProperty("级组id")
    private Long gradeId;

    @ApiModelProperty("级组名称")
    private String gradeName;

    @ApiModelProperty("班级ID")
    private Long classId;

    @ApiModelProperty("班级名称")
    private String className;

    @ApiModelProperty("学生ID")
    private Long studentId;

    @ApiModelProperty("学生名称")
    private String studentName;

    @ApiModelProperty("座位号")
    private Integer seatNo;

    @ApiModelProperty("活动名称")
    private String activityName;

    @ApiModelProperty("机构名称")
    private String organization;

    @ApiModelProperty("服务日期")
    private LocalDate serviceDate;

    @ApiModelProperty("开始时间")
    private LocalTime startTime;

    @ApiModelProperty("结束时间")
    private LocalTime endTime;

    @ApiModelProperty("服务时数")
    private Double serviceHours;

    @ApiModelProperty("服务时数(秒)")
    private Long serviceSeconds;

    @ApiModelProperty("服务性质")
    private String serviceNature;

    @ApiModelProperty("服务类别")
    private String serviceType;
}