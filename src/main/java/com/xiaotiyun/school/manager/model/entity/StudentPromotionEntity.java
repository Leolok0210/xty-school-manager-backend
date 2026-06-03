package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;

@TableName("student_promotion")
@Data
public class StudentPromotionEntity extends BaseEntity {
    /**
     * 学校ID
     */
    private Long schoolId;

    /**
     * 学年（格式：YYYY-YYYY）
     */
    private String schoolYear;

    /**
     * 学生ID（关联student表）
     */
    private Long studentId;

    /**
     * 班级ID（关联sys_class表）
     */
    private Long classId;

    /**
     * 班内号
     */
    private Integer seatNo;

    /**
     * 升降级类型（1-升级 2-留级 3-带科）
     */
    private Integer promotionType;
}