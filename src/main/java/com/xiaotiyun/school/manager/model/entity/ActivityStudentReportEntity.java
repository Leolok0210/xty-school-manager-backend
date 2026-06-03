package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 活动已匹配表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("activity_student_report")
public class ActivityStudentReportEntity extends BaseEntity {
    
    /**
     * 活动id
     */
    private Long activityId;
    
    /**
     * 课程id
     */
    private Long lensonId;
    
    /**
     * 用户id
     */
    private Long studentId;
    
    /**
     * 类型（1.预先导入 2.分配，3 一次报名志愿录入，4 二次报名志愿录入）
     * @see com.xiaotiyun.school.manager.basic.enums.LeisureActivityMatchTypeEnum
     */
    private Integer type;
    
    /**
     * 第几志愿数
     */
    private Long volunteerType;
    
    /**
     * 状态（1.匹配 2.发布）
     */
    private Integer status;
} 