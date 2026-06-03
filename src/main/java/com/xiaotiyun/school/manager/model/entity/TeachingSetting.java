package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;

@Data
@TableName("teaching_setting")
public class TeachingSetting extends BaseEntity {
    /**
     * 学年
     */
    private String sid; // 修改: Long 改为 String

    /**
     * 学校id
     */
    private Long schoolId;

    /**
     * 班级 id
     */
    private Long classId;

    /**
     * 科目 id
     */
    private Long subjectId;

    /**
     * 任教老师id
     */
    private Long teacherId;
}