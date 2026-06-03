package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("leisure_activity_courses_record")
public class LeisureActivityCoursesRecordEntity extends BaseEntity {

    /**
     * 学校ID
     */
    private Long schoolId;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 课程名称
     */
    private String name;

    /**
     * 教师名称
     */
    private String teacher;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 教室名称
     */
    private String classroom;

    /**
     * 教室ID，为空时为自定义地址
     */
    private Long classroomId;

    /**
     * 课程名额
     */
    private Integer quotaTotal;

    /**
     * 课程次数
     */
    private Integer coursesNum;

    /**
     * 活动状态(0-不开放 1-开放)
     */
    private Integer status;

    /**
     * 上课时间，JSON格式，每条记录包含周几、开始时间和结束时间
     */
    private String courseTime;
}
