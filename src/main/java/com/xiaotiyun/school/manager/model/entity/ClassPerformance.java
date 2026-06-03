package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("class_performance")
public class ClassPerformance extends BaseEntity {
    /**
     * 所属学年
     */
    private String sid;

    /**
     * 学校ID
     */
    private Long schoolId;

    /**
     * 班级id
     */
    private Long classId;

    /**
     * 学期
     */
    private Long term;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 上课日期
     */
    private LocalDateTime classDate;

    /**
     * 节数
     */
    private String classSection;

    /**
     * 课堂表现
     */
    private String performance;

    /**
     * 课堂表现ID
     */
    private Long performanceId;

    private Long userId;
}