package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 平时成绩类型表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student_usually_type")
public class StudentUsuallyTypeEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 学校ID
     */
    @ApiModelProperty(value = "学校ID")
    private Long schoolId;

    /**
     * 平时成绩类型名称
     */
    @ApiModelProperty(value = "平时成绩类型名称")
    private String typeName;
}
