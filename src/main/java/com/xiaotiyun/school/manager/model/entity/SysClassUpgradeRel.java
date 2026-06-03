package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;

/**
 * 升班学生表
 * @TableName sys_class_upgrade_rel
 */
@TableName(value ="sys_class_upgrade_rel")
@Data
public class SysClassUpgradeRel extends BaseEntity {

    /**
     * 班级
     */
    private Long classId;

    /**
     * 座位号
     */
    private Integer seatNo;

    /**
     * 学生id
     */
    private Long studentId;
}