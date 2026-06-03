package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("grade_record_class_setting")
public class GradeRecordClassSettingEntity extends BaseEntity {
    
    /**
     * 学校ID
     */
    private Long schoolId;
    
    /**
     * 学年
     */
    private String schoolYear;
    
    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 级组ID
     */
    private Long gradeId;
    
    /**
     * 是否可录入考试成绩(0:否 1:是)
     */
    private Boolean canRecordExam;
    
    /**
     * 是否可录入毕业成绩(0:否 1:是)
     */
    private Boolean canRecordGraduation;

    /**
     * 是否可录入德育(0:否 1:是)
     */
    private Boolean canRecordMoralEducation;

    /**
     * 是否可录入义工(0:否 1:是)
     */
    private Boolean canRecordVolunteer;

    /**
     * 是否可录入操行(0:否 1:是)
     */
    private Boolean canRecordConduct;
}