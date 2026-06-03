package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_dept")
public class DeptEntity extends BaseEntity {

    /**
     * 学校ID
     */
    private Long schoolId;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 层级，最多20级
     */
    private Integer level;
}