package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_school")
public class SchoolEntity extends BaseEntity {
    /**
     * 学校名称
     */
    private String name;
    
    /**
     * 学校编号
     */
    private String code;
    
    /**
     * 省份
     */
    private String province;
    
    /**
     * 城市
     */
    private String city;
    
    /**
     * 区县
     */
    private String district;
    
    /**
     * 详细地址
     */
    private String address;
    
    /**
     * 学校类型,多个类型用逗号分隔
     * 1:幼稚园 2:小学 3:中学
     */
    private String schoolType;
    
    /**
     * 有效期截止时间,为空表示永久有效
     */
    private LocalDateTime expireTime;
    
    /**
     * 备注
     */
    private String remark;

    /**
     * 企微对接类型,1-企微内部应用,2-企微三方应用
     */
    private Integer entWechatType;

    /**
     * 企微绑定名称
     */
    private String entWechatName;

    /**
     * 是否发起过健康申报，0-未申报，1-已申报
     */
    private Integer isHealthDeclared;

    /**
     * 渠道id
     */
    private Long channelId;

    /**
     * 渠道学校ID
     */
    private Long channelSchoolId;

} 