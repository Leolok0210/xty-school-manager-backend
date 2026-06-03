package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("课堂表现详情返回信息")
public class ClassPerformanceDetailResModel {
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("所属学年")
    private String sid;

    @ApiModelProperty("学段name")
    private String termName;

    @ApiModelProperty("学校ID")
    private Long schoolId;

    @ApiModelProperty("班级id")
    private Long classId;

    @ApiModelProperty("学生姓名")
    private String studentName;

    @ApiModelProperty("座位号")
    private Integer seatNo;

    @ApiModelProperty("班级name")
    private String className;

    @ApiModelProperty("学期")
    private Long term;

    @ApiModelProperty("学生id")
    private Long studentId;

    @ApiModelProperty("上课日期")
    private LocalDateTime classDate;

    @ApiModelProperty("节数")
    private String classSection;

    @ApiModelProperty("课堂表现")
    private String performance;

    @ApiModelProperty("登记人id")
    private Long userId;

    @ApiModelProperty("登记人")
    private String userName;

    @ApiModelProperty("组级name")
    private String gradeGroupName;
}