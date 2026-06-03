package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 活动学生选课情况查询请求模型
 */
@Data
@ApiModel("活动学生选课情况查询请求模型")
public class ActivityStudentReportQueryReqModel extends PageReqModel {

    @NotNull(message = "学校ID不能为空")
    @ApiModelProperty(value = "学校ID", required = true)
    private Long schoolId;

    @NotNull(message = "学年不能为空")
    @ApiModelProperty(value = "学年", required = true)
    private String schoolYear;

    @NotNull(message = "学部不能为空")
    @ApiModelProperty(value = "学部(1:幼稚园 2:小学 3:中学)", required = true)
    private Integer department;

    @ApiModelProperty("班级ID")
    private Long classId;

    @ApiModelProperty("班级ID")
    private List<Long> classIds;

    @ApiModelProperty("学生姓名")
    private String studentName;

    @ApiModelProperty("学生编号")
    private String studentNo;

    private Long activityId;

}