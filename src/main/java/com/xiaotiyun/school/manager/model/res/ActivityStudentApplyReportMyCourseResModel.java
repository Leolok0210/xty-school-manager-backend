package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 我的课程列表响应模型
 */
@Data
@ApiModel("我的课程列表响应模型")
public class ActivityStudentApplyReportMyCourseResModel {

    @ApiModelProperty("课程ID")
    private Long lensonId;

    @ApiModelProperty("课程名称")
    private String lensonName;

    @ApiModelProperty("老师名称")
    private String teacherName;

    @ApiModelProperty("上课地点")
    private String address;

    @ApiModelProperty("课程次数")
    private Integer courseCount;

    @ApiModelProperty("上课时间")
    private String courseTime;
} 