package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 活动学生报告导出响应模型
 */
@Data
@ApiModel("活动学生报告导出响应模型")
public class ActivityStudentReportExportResModel {
    
    @ApiModelProperty("导出文件URL")
    private String url;
} 