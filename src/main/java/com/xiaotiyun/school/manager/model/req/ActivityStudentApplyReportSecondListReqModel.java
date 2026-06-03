package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 二次报名管理列表请求模型
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("二次报名管理列表请求模型")
public class ActivityStudentApplyReportSecondListReqModel extends PageReqModel {

    @NotNull(message = "活动ID不能为空")
    @ApiModelProperty(value = "活动ID", required = true)
    private Long activityId;

    @ApiModelProperty("班级ID")
    private Long classId;

    @ApiModelProperty("学生姓名")
    private String studentName;

    @ApiModelProperty("学生编号")
    private String studentNo;

    @ApiModelProperty("匹配状态（-1全部，1成功，2失败）")
    private Integer matchStatus;
} 