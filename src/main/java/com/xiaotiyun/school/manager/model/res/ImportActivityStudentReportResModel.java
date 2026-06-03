package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 导入活动匹配响应模型
 */
@Data
@ApiModel("导入活动匹配响应")
public class ImportActivityStudentReportResModel {

    @ApiModelProperty("任务ID")
    private String taskId;
} 