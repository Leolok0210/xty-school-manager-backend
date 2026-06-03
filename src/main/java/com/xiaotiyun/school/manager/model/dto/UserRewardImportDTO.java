package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRewardImportDTO {
    /**
     * 学生id
     */
    private Long studentId;
    /**
     * 学生姓名
     */
    private String studentName;
    /**
     * 学生编号
     */
    private String studentCode;
    /**
     * 会议通过日期
     */
    private LocalDate meetingDate;
    /**
     * 原因
     */
    private String rewardReason;
    /**
     * 类型(1.大过;2.小过;3.缺点;4.大功;5.小功;6.优点)
     */
    private Integer type;
    /**
     * 次数
     */
    private Integer frequency;
    /**
     * 备注
     */
    private String remark;
}
