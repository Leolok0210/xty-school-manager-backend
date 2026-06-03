package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *  级组表
 * @TableName grade_group
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="grade_group")
@Data
public class GradeGroup extends BaseEntity {

    /**
     *  学部（幼稚园、小学、中学）
     */
    private Long department;

    /**
     *  级组名称 
     */
    private String gradeGroupName;

    /**
     *  学校id 
     */
    private Long schoolId;

    /**
     * 预设年级
     */
    private String grade;

    /**
     * 文科理科：0-不分科；1-专业分科；2-文理分科 3-理工商科
     */
    private Integer professionalSubject;

    /**
     * 文理分科类型：1-按班级分科；2-按学生分科
     */
    private Integer artsScienceType;
}