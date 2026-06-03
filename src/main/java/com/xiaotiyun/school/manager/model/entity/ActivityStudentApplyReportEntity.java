package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 活动报名表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("activity_student_apply_report")
public class ActivityStudentApplyReportEntity extends BaseEntity {
    
    /**
     * 活动id
     */
    private Long activityId;
    
    /**
     * 用户id
     */
    private Long studentId;
    
    /**
     * 类型（1.一次报名 2.二次报名）
     */
    private Integer type;
    
    /**
     * 状态（1.待匹配 2.已匹配 3.匹配失败）
     */
    private Integer status;
} 