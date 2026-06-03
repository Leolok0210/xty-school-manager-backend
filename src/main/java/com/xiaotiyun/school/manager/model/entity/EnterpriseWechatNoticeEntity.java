package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("enterprise_wechat_notice")
public class EnterpriseWechatNoticeEntity extends BaseEntity {
    /**
     * 学年
     */
    private String schoolYear;
    /**
     * 学校ID
     */
    private Long schoolId;
    /**
     * 业务ID(类型为余暇活动时必传)
     */
    private Long businessId;
    /**
     * 通知类型(1-自定义通知,2-余暇活动,3-健康申报)
     */
    private Integer noticeType;
    /**
     * 发送对象(1-全部,2-学生,3-家长)
     */
    private Integer targetType;
    /**
     * 筛选值(JSON格式)
     */
    private String filterValue;
    /**
     * 标题
     */
    private String title;
    /**
     * 通知内容
     */
    private String noticeContent;
    /**
     * 状态(0-待发送,1-已发送)
     */
    private Integer status;
    /**
     * 类型(1-小程序,2-H5)
     */
    private Integer type;
    /**
     * 发送时间
     */
    private LocalDateTime sendTime;
    /**
     * 是否系统创建
     */
    private Boolean isSystem;
    /**
     * 创建人ID
     */
    private Long creatorId;
}