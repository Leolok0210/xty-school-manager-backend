package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 活动学生报告导出请求模型
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("活动学生报告导出请求模型")
public class ActivityStudentReportExportReqModel extends PageReqModel {
    
    @ApiModelProperty(value = "学校ID", required = true)
    @NotNull(message = "学校ID不能为空")
    private Long schoolId;
    
    @ApiModelProperty(value = "活动ID", required = true)
    @NotNull(message = "活动ID不能为空")
    private Long activityId;
} 