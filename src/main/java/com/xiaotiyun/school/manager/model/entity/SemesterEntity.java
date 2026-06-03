package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_semester")
public class SemesterEntity extends BaseEntity {
    /**
     * 学年(格式:2025-2026)
     */
    private String schoolYear;

    /**
     * 学部(1:幼稚园 2:小学 3:中学)
     */
    private Integer department;

    /**
     * 学段名称
     */
    private String name;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 学校ID
     */
    private Long schoolId;
} 