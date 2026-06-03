package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class UserRewardUpdateReqModel {
    @ApiModelProperty("奖励id")
    private Long id;
    @ApiModelProperty(value = "所属学年",required = true)
    private String sid; // 修改: Long 改为 String

    @ApiModelProperty(value = "学校ID",required = true)
    private Long schoolId;

    @NotNull(message = "学期不能为空")
    @ApiModelProperty(value = "学期",required = true)
    private Long term;

    @NotNull(message = "日期不能为空")
    @ApiModelProperty(value = "日期",example = "2024-10-01T12:34:56",required = true)
    private LocalDateTime date;

    @NotNull(message = "学生id不能为空")
    @ApiModelProperty(value = "学生id",required = true)
    private Long studentId;

    @NotNull(message = "类型不能为空")
    @ApiModelProperty(value = "类型 1奖励 2惩罚",required = true)
    private Integer type;

    @Size(max = 200,message = "奖励原因不能超过200个字符")
    @NotNull(message = "奖励原因不能为空")
    @ApiModelProperty(value = "奖励原因",required = true)
    private String rewardReason;

    @ApiModelProperty("大功")
    private Integer maxReward;

    @ApiModelProperty("中功")
    private Integer midReward;

    @ApiModelProperty("优点")
    private Integer minReward;
}