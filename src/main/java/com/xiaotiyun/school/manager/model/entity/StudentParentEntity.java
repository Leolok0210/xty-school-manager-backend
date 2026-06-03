package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学生家长信息实体类
 * 对应表名: student_parent
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student_parent")
public class StudentParentEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 学校ID
     */
    @TableField("school_id")
    private Long schoolId;

    /**
     * 学生ID
     */
    @TableField("student_id")
    private Long studentId;

    /**
     * 家长名称
     */
    @TableField("parent_name")
    private String parentName;

    /**
     * 家长手机号
     */
    @TableField("parent_phone")
    private String parentPhone;

    /**
     * 家长与学生关系
     */
    @TableField("parent_relation")
    private String parentRelation;

    /**
     * 家长类型:1-父亲关系;2-母亲关系;3-监护人关系;4-其他家长关系;
     */
    @TableField("parent_type")
    private String parentType;

    /**
     * 是否接受短讯
     */
    @TableField("is_accept_sms")
    private Boolean acceptSms;

    /**
     * '职业
     */
    @TableField("job")
    private String job;

    /**
     * '任职单位
     */
    @TableField("job_unit")
    private String jobUnit;

    /**
     * '与监护人同住
     */
    @TableField("is_with_guardian")
    private Boolean withGuardian;

    /**
     * '监护人流动电话
     */
    @TableField("guardian_mobile")
    private String guardianMobile;

    /**
     * '地址区域ID
     */
    @TableField("address_area_id")
    private String addressAreaId;

    /**
     * '监护人住址
     */
    @TableField("guardian_address")
    private String guardianAddress;
}
