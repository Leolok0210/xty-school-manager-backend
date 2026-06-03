package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "学生日期记录响应模型")
public class StudentDateRecordResModel {
    @ApiModelProperty(value = "记录ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "学校ID", example = "101")
    private Long schoolId;

    @ApiModelProperty(value = "学生ID", example = "201")
    private Long studentId;

    @ApiModelProperty(value = "入学时间", example = "2023-10-01 08:00:00")
    private String inTime;

    @ApiModelProperty(value = "退学时间", example = "2023-10-01 17:00:00")
    private String outTime;

    @ApiModelProperty(value = "退学原因", example = "会议")
    private String outReason;

    @ApiModelProperty(value = "升级情况", example = "无")
    private String escalationSituation;
}