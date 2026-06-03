package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;

@Data
@TableName("student_quality_score")
public class StudentQualityScore extends BaseEntity {
    /**
     * 学年
     */
    private String sid;

    /**
     * 学段
     */
    private Long term;

    /**
     * 学生姓名
     */
    private Long studentId;

    /**
     * 班级
     */
    private Long classId;

    /**
     * 素质项目1评分 * 100
     */
    private Long qualityProjectScore;

    /**
     * 素质项目id
     */
    private Long qualityProjectId;

    // 添加 schoolId 字段
    private Long schoolId;
}