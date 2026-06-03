package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CourseScheduleHomeClassListResModel {
    @ApiModelProperty("学部")
    private Integer department;

    @ApiModelProperty("班级序号")
    private Integer classSerialNumber;

    @ApiModelProperty("级组名称")
    private String gradeName;

    @ApiModelProperty("班级名称")
    private String className;

    @ApiModelProperty("课程信息")
    private List<CourseScheduleClassListDetailsResModel> courseDetails;
}