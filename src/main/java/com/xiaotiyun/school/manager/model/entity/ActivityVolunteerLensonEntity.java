package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 志愿课程表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("activity_volunteer_lenson")
public class ActivityVolunteerLensonEntity extends BaseEntity {
    
    /**
     * 课程id
     */
    private Long lensonId;
    
    /**
     * 用户id
     */
    private Long studentId;
    
    /**
     * 报名表id
     */
    private Long applyId;
    
    /**
     * 志愿数
     */
    private Long volunteerType;

    /**
     * 活动id
     */
    private Long activityId;
} 