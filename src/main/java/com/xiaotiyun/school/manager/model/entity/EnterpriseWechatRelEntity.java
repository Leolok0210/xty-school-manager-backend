package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 企业微信关联关系表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("enterprise_wechat_rel")
public class EnterpriseWechatRelEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 关联id
     */
    @TableField("rel_id")
    private Long relId;

    /**
     * 关联类型 1-级组 2-班级 3-学生 4-家长
     */
    @TableField("type")
    private Integer type;

    /**
     * 学校ID
     */
    @TableField("school_id")
    private Long schoolId;

    /**
     * 关联企业微信id
     */
    @TableField("wx_id")
    private String wxId;

    /**
     * 学年
     */
    @TableField("school_year")
    private String schoolYear;

}