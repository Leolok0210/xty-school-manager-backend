package com.xiaotiyun.school.manager.model.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@ApiModel(description = "余暇活动记录返回对象")
public class LeisureActivityRecordResModel {
    /**
     * id
     */
    @ApiModelProperty(value = "主键id")
    private Long id;
    /**
     * 学校 ID
     */
    @ApiModelProperty(value = "学校 ID")
    private Long schoolId;

    /**
     * 学年
     */
    @ApiModelProperty(value = "学年")
    private String schoolYear;

    /**
     * 学部 (1: 幼稚园, 2: 小学, 3: 中学)
     */
    @ApiModelProperty(value = "学部 (1: 幼稚园, 2: 小学, 3: 中学)")
    private Integer department;

    /**
     * 学段 ID
     */
    @ApiModelProperty(value = "学段 ID")
    private Long semesterId;

    /**
     * 学段 ID
     */
    @ApiModelProperty(value = "学段名称")
    private String semesterName;

    /**
     * 活动名称
     */
    @ApiModelProperty(value = "活动名称")
    private String name;

    /**
     * 课程数量
     */
    @ApiModelProperty(value = "课程数量")
    private Long courseNum;

    /**
     * 可选志愿数
     */
    @ApiModelProperty(value = "可选志愿数")
    private Integer volunteerNum;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /**
     * 二次报名结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "二次报名结束时间")
    private Date secondEndTime;

    /**
     * 报名须知
     */
    @ApiModelProperty(value = "报名须知")
    private String remark;

    /**
     * 活动状态 (0-未发布, 1-已发布, 2-已截止，3-二次已截止（只有list接口有）)
     */
    @ApiModelProperty(value = "活动状态 (0-未发布, 1-已发布, 2-一次已截止，3-二次已截止（只有list接口有）)")
    private Integer status;

    /**
     * 公布状态 (0-未公布, 1-已公布, 2-二次公布)
     */
    @ApiModelProperty(value = "公布状态 (0-未公布, 1-已公布, 2-二次公布)")
    private Integer publishStatus;

    /**
     * 是否需要报名，学生端使用
     */
    @ApiModelProperty(value = "是否需要报名，学生端使用，0-需要报名，1-不需要报名")
    private Integer needSignUp;

    @ApiModelProperty(value = "是否开启企微通知，0-未开启，1-已开启")
    private Integer openWechatNotice;

    @ApiModelProperty(value = "开始通知发送时间")
    private LocalDateTime startNoticeTime;

    @ApiModelProperty(value = "截止通知发送时间")
    private LocalDateTime endNoticeTime;
}
