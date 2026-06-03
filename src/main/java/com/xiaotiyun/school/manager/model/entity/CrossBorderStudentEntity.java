package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 跨境学生信息实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cross_border_student")
public class CrossBorderStudentEntity extends BaseEntity {
    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 入境内地证件类型
     */
    private Integer mainlandCertificateType;
    
    /**
     * 入境内地证件类型其他描述
     */
    private String mainlandCertificateTypeOther;
    
    /**
     * 入境内地证件号码
     */
    private String mainlandCertificateNumber;
    
    /**
     * 陪同人中文姓名
     */
    private String companionChineseName;
    
    /**
     * 陪同人外文姓名或译音
     */
    private String companionForeignName;
    
    /**
     * 陪同人性别
     */
    private Integer companionGender;
    
    /**
     * 陪同人出生日期
     */
    private LocalDate companionBirthDate;
    
    /**
     * 入境澳门证件类型
     */
    private Integer macauCertificateType;
    
    /**
     * 入境澳门证件类型其他描述
     */
    private String macauCertificateTypeOther;
    
    /**
     * 往来港澳通行证签注种类
     */
    private Integer hkMacauPassVisaType;
    
    /**
     * 入境澳门证件号码
     */
    private String macauCertificateNumber;
    
    /**
     * 入境内地证件类型
     */
    private Integer mainlandEntryCertificateType;
    
    /**
     * 入境内地证件类型其他描述
     */
    private String mainlandEntryCertificateTypeOther;
    
    /**
     * 入境内地证件号码
     */
    private String mainlandEntryCertificateNumber;
}