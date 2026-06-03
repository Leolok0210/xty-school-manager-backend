package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 余暇活动课程操作记录实体类（Entity）
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("leisure_course_op_record")
public class LeisureCourseOpRecordEntity extends BaseEntity {
    /**
     * 学校ID
     */
    private Long schoolId;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 课程ID
     */
    private Long coursesId;

    /**
     * 页面来源，1-预先导入，2-已匹配，3-无课程，4-二次报名
     * @see com.xiaotiyun.school.manager.basic.enums.PageSourceEnum
     */
    private Integer sourceId;

    /**
     * 学生名称
     */
    private String studentName;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 操作类型(0-批量导入 1-移除 2-批量移除 3-分配 4-批量分配 5-转班 6-批量转班 7-转入 8-批量转入)
     * @see com.xiaotiyun.school.manager.basic.enums.LeiSureOperationTypeEnum
     */
    private Integer operationType;
}
