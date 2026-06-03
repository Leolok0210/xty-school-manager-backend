package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class ActApprovalInstancePendingReqModel extends PageReqModel {
    @NotNull(message = "审批类型不能为空")
    @ApiModelProperty(value = "审批类型(1.教师请假；2.教师公务)", required = true)
    private Integer processType;
    @ApiModelProperty(value = "教师id")
    private Long teacherId;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "开始时间")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "结束时间")
    private LocalDate endDate;
}