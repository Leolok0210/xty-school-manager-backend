package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student_graduate_enroll")
public class StudentGraduateEnrollEntity extends BaseEntity {
    /**
     * 学校id
     */
    private Long schoolId;

    /**
     * 班级id
     */
    private Long classId;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 类型(1.留级；2.毕业)
     */
    private Integer type;

    /**
     * 毕业类型(1.升学；2.就业)
     */
    private Integer graduateType;

    /**
     * 就读地点
     */
    private String schoolAddress;

    /**
     * 就读院校
     */
    private String faculty;

    /**
     * 就读科系
     */
    private String department;

    /**
     * 职业
     */
    private String job;
}