package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActProcessTemplatePageResModel {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("审批名称")
    private String processName;
    @ApiModelProperty("审批类型(1.教师请假；2.教师公务)")
    private Integer processType;
    @ApiModelProperty("发起人范围json(为空时表示所有人都可发起)")
    private String initiatorScope;
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}