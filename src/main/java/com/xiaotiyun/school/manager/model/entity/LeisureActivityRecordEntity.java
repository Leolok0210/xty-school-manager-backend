package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("leisure_activity_record") // 表名
public class LeisureActivityRecordEntity extends BaseEntity {
    /**
     * 学校 ID
     */
    @TableField("school_id")
    private Long schoolId;

    /**
     * 学年
     */
    @TableField("school_year")
    private String schoolYear;

    /**
     * 学部 (1: 幼稚园, 2: 小学, 3: 中学)
     */
    @TableField("department")
    private Integer department;

    /**
     * 学段 ID
     */
    @TableField("semester_id")
    private Long semesterId;

    /**
     * 活动名称
     */
    @TableField("name")
    private String name;

    /**
     * 可选志愿数
     */
    @TableField("volunteer_num")
    private Integer volunteerNum;

    /**
     * 发布时间
     */
    @TableField("publish_time")
    private Date publishTime;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private Date endTime;

    /**
     * 二次报名结束时间
     */
    @TableField("second_end_time")
    private Date secondEndTime;

    /**
     * 报名须知
     */
    @TableField("remark")
    private String remark;

    /**
     * 活动状态 (0-未发布, 1-已发布, 2-一次已截止，3-二次已截止)注：2和3是查询出来后计算的
     */
    @TableField("status")
    private Integer status;

    /**
     * 公布状态 (0-未公布, 1-已公布, 2-二次公布)
     */
    @TableField("publish_status")
    private Integer publishStatus;

    /**
     * 是否开启企微通知，0-未开启，1-已开启
     */
    @TableField("open_wechat_notice")
    private Integer openWechatNotice;

    /**
     * 开始通知发送时间
     */
    @TableField("start_notice_time")
    private LocalDateTime startNoticeTime;

    /**
     * 截止通知发送时间
     */
    @TableField("end_notice_time")
    private LocalDateTime endNoticeTime;

    /**
     * 抽签状态，0-未抽取，1-一次抽签，2-二次抽签
     */
    @TableField("draw_status")
    private Integer drawStatus;
}

