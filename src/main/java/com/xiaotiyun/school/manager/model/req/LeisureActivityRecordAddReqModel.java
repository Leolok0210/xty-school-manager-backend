package com.xiaotiyun.school.manager.model.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@ApiModel("余暇活动记录添加请求对象")
public class LeisureActivityRecordAddReqModel {

    /**
     * id
     */
    @ApiModelProperty(value = "主键id，更新和复制时必传")
    private Long id;
    /**
     * 学校 ID
     */
    @ApiModelProperty(value = "学校 ID")
    private Long schoolId;

    /**
     * 学年
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学年")
    private String schoolYear;

    /**
     * 学部 (1: 幼稚园, 2: 小学, 3: 中学)
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学部 (1: 幼稚园, 2: 小学, 3: 中学)")
    private Integer department;

    /**
     * 学段 ID
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学段 ID")
    private Long semesterId;

    /**
     * 活动名称
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @Size(max = 20, message =  LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "活动名称")
    private String name;

    /**
     * 可选志愿数
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "可选志愿数")
    private Integer volunteerNum;

    /**
     * 开始时间
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /**
     * 结束时间
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /**
     * 报名须知
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "报名须知")
    private String remark;

    @ApiModelProperty(value = "是否开启企微通知，0-未开启，1-已开启")
    private Integer openWechatNotice;

    @ApiModelProperty(value = "开始通知发送时间")
    private LocalDateTime startNoticeTime;

    @ApiModelProperty(value = "截止通知发送时间")
    private LocalDateTime endNoticeTime;
}
