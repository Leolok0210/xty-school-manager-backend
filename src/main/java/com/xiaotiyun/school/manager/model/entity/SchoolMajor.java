package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;

@Data
@TableName("school_major")
public class SchoolMajor extends BaseEntity {
//    /**
//     * 所属学年
//     */
//    private String sid; // 修改: Long 改为 String

    /**
     * 专业名称
     */
    private String majorName;

    /**
     * 学部(1:幼稚园 2:小学 3:中学)
     */
    private Integer departmentId;

    private Long schoolId;

    private String majorSubjects;
}