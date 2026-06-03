package com.xiaotiyun.school.manager.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("学生迟到统计返回模型")
public class StudentLateCountResDTO {
    
    @ApiModelProperty("学生ID")
    private Long studentId;
    
    @ApiModelProperty("迟到总次数")
    private Integer lateCount;

} 