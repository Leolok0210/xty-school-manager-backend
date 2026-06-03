package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student_usually_task")
public class StudentUsuallyTaskEntity extends BaseEntity {
    /**
     * 学校id
     */
    private Long schoolId;

    /**
     * 平时成绩类型id
     */
    private Long typeId;

    /**
     * 测验名称
     */
    private String name;

    /**
     * 次数
     */
    private Integer frequency;

    /**
     * 测验时间
     */
    private LocalDate testDate;

    /**
     * 学段id
     */
    private Long periodId;

    /**
     * 班级id
     */
    private Long classId;

    /**
     * 科目id
     */
    private Long subjectId;

    /**
     * 备注
     */
    private String remark;
    /**
     * 更新人id
     */
    private Long updateId;
} 