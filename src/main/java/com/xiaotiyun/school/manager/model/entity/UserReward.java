package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("user_reward")
public class UserReward extends BaseEntity {
    /**
     * 所属学年
     */
    private String sid; // 修改: Long 改为 String

    /**
     * 学校ID
     */
    private Long schoolId;

    /**
     * 学期
     */
    private Long term;

    /**
     * 日期
     */
    private LocalDateTime date;

    /**
     * 会议日期
     */
    private LocalDate meetingDate;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 类型 1奖励 2惩罚
     */
    private Integer type;

    /**
     * 奖励类型,0-常规类型,1-竞赛奖励类型
     */
    private Integer registerType;

    /**
     * 奖励原因
     */
    private String rewardReason;

    /**
     * 大功 大过
     */
    private Integer maxReward;

    /**
     * 中功 小过
     */
    private Integer midReward;

    /**
     * 优点 缺点
     */
    private Integer minReward;

    /**
     * 是否系统自动登记(0-否，1-是)
     */
    private Integer isAuto;

    /**
     * 自动计算类型，0-课堂表现，1-欠交作业，2-仪表不符，3-迟到次数，4-欠课本，5-缺席，6-欠回条
     */
    private Integer autoType;

    /**
     * 外部竞赛记录id
     */
    private Long externalCompetitionRecordId;

    /**
     * 备注
     */
    private String remark;
}