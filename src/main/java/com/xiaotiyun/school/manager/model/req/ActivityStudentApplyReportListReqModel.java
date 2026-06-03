package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 活动学生管理列表请求模型
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("活动学生管理列表请求模型")
public class ActivityStudentApplyReportListReqModel extends PageReqModel {

    @NotNull(message = "活动ID不能为空")
    @ApiModelProperty(value = "活动ID", required = true)
    private Long activityId;

    @ApiModelProperty("班级ID")
    private Long classId;

    @ApiModelProperty("学生姓名")
    private String studentName;

    @ApiModelProperty("学生编号")
    private String studentNo;

    // @ApiModelProperty("类型（-1全部，1二次报名）")
    // private Integer type;
} 