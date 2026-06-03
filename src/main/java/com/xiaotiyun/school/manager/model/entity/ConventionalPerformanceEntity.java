package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 常规表现表实体类
 * 对应数据库表：conventional_performance
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("conventional_performance")
public class ConventionalPerformanceEntity extends BaseEntity {

    /**
     * 学校id
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
     * 所属学年
     */
    private String sid;

    /**
     * 事件日期
     */
    private LocalDate date;

    /**
     * 类型(1.上课违规;2.欠作业;3.仪表不符;5.欠课本;7.欠回条)
     */
    private Integer type;

    /**
     * 次数
     */
    private Integer frequency;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    private Long createId;
}