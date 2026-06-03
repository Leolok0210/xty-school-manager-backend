package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("学生请假统计返回模型")
public class StudentLeaveStatisticsResModel {
    
    @ApiModelProperty("学生ID")
    private Long studentId;
    
    @ApiModelProperty("请假类型")
    private Integer leaveType;
    
    @ApiModelProperty("请假总节数")
    private Integer totalPeriods;
} 