package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student_family")
public class StudentFamilyEntity extends BaseEntity {
    
    /**
     * 学生ID
     */
    private Long studentId;
    
    // 父亲信息
    private String fatherName;
    private String fatherPhone;
    private Integer fatherSms;
    private String fatherOccupation;
    private String fatherCompany;
    
    // 母亲信息
    private String motherName;
    private String motherPhone;
    private Integer motherSms;
    private String motherOccupation;
    private String motherCompany;
    
    // 监护人信息
    private String guardianName;
    private String guardianPhone;
    private Integer guardianSms;
    private String guardianOccupation;
    private String guardianCompany;
    private Integer guardianRelation;
    private Integer liveWithGuardian;
    private String guardianMobile;
    private String guardianAddressAreaId;
    private String guardianAddress;

    // 紧急联系人信息
    private String emergencyContact;
    private Integer emergencyRelation;
    private String emergencyPhone;
    private String emergencyAddressAreaId;
    private String emergencyAddress;

    // 第二紧急联系人信息
    private String secondEmergencyContact;
    private Integer secondEmergencyRelation;
    private String secondEmergencyPhone;
    private String secondEmergencyAddressAreaId;
    private String secondEmergencyAddress;

    /**
     * 家庭状况
     */
    private String familyStatus;

    /**
     * 备注
     */
    private String remark;
}