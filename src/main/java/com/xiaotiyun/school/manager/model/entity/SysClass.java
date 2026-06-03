package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;

@Data
@TableName("sys_class")
public class SysClass extends BaseEntity {
    /**
     * 学部
     */
    private Integer department;

    /**
     * 级组
     */
    private Long gradeGroup;

    /**
     * 班级序号
     */
    private Integer classSerialNumber;

    private String classNumber;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 是否专业版(0.否,1.是)
     */
    private Integer professionalVersion;

    /**
     * 文理科(1文科 2理科) 理工商科(1理工科 2理科 3商科)
     */
    private Integer artsScience;

    /**
     * 专业
     */
    private Long professionalId;

    /**
     * 班主任
     */
    private Long headTeacher;

    /**
     * sid
     */
    private String sid;

    // 增加 schoolId 字段
    private Long schoolId;

    private Integer upgrade;
}