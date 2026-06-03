package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class UserRewardAddStudentReqModel {
    @NotNull(message = "会议通过日期不能为空")
    @ApiModelProperty(value = "会议通过日期", example = "2024-10-01", required = true)
    private LocalDate meetingDate;
    @NotNull(message = "学生id不能为空")
    @ApiModelProperty(value = "学生id", required = true)
    private Long studentId;
    @Size(max = 200, message = "奖励原因不能超过200个字符")
    @NotNull(message = "奖励原因不能为空")
    @ApiModelProperty(value = "奖励原因", required = true)
    private String rewardReason;
    @NotNull(message = "类型不能为空")
    @ApiModelProperty(value = "类型(1.大过;2.小过;3.缺点;4.大功;5.小功;6.优点)", required = true)
    private Integer type;
    @NotNull(message = "次数不能为空")
    @ApiModelProperty(value = "次数", required = true)
    private Integer frequency;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty(value = "比赛记录ID,当奖励类型为：1-竞赛奖励类型，时必传")
    private Long externalCompetitionRecordId;
}