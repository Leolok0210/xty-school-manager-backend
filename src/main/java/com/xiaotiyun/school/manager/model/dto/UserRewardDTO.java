package com.xiaotiyun.school.manager.model.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserRewardDTO {

    /**
     * 日期
     */
    private LocalDateTime date;

    /**
     * 日期
     */
    private LocalDateTime date1;

    /**
     * 会议日期
     */
    private LocalDate meetingDate;

    /**
     * 学生id
     */
    private Long studentId;


    /**
     * 奖励类型,0-常规类型,1-竞赛奖励类型
     */
    private Integer registerType;

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
     * 自动计算类型，0-课堂表现，1-欠交作业，2-仪表不符，3-迟到次数
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


    /**
     * 累计次数
     */
    private int number;

    /**
     * 累计次数
     */
    private int number1;

    /**
     * 小结
     */
    private List<UserRewardDetailsDTO> details;

    /**
     * 小结 ketang
     */
    private List<UserRewardDetailsDTO> details1;

    private int priNumber;

}