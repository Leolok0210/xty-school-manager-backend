package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("subject")
public class Subject extends BaseEntity {

    /**
     * 科目编号
     */
    private String subjectNumber;

    /**
     * 科目名称
     */
    private String subjectName;

    /**
     * 科目英文名称
     */
    private String subjectEnglishName;

    /**
     * 单位
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer unit;

    // 增加 schoolId 字段
    private Long schoolId;


    //范围 [1,2,3]',
    //  PRIMARY KEY (`id`),
    private String scope;

}