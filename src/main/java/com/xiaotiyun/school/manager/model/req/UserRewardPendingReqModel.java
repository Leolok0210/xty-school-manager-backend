package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class UserRewardPendingReqModel extends PageReqModel {
    @NotBlank(message = "所属学年不能为空")
    @ApiModelProperty(value = "所属学年", required = true)
    private String sid;
    @NotNull(message = "学期不能为空")
    @ApiModelProperty(value = "学期", required = true)
    private Long term;
    @NotNull(message = "类型不能为空")
    @ApiModelProperty(value = "类型 1奖励 2惩罚", required = true)
    private Integer type;
    @ApiModelProperty("学部(1:幼稚园 2:小学 3:中学)")
    private Integer department;
    @ApiModelProperty("班级id")
    private Long classId;
    @ApiModelProperty("学生信息(姓名/编号)")
    private String studentInfo;
    @ApiModelProperty("原因")
    private String rewardReason;
    @ApiModelProperty(value = "开始时间", example = "2024-10-01")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @ApiModelProperty(value = "结束时间", example = "2024-10-01")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}