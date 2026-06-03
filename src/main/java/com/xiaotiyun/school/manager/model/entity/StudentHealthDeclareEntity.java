package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 学生健康申报表实体类
 * @author generated
 * @since 2023-09-01
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("student_health_declare")
public class StudentHealthDeclareEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 学年
     */
    @TableField("school_year")
    private String schoolYear;

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
     * 学生名称
     */
    @TableField("student_name")
    private String studentName;

    /**
     * 学生学号
     */
    @TableField("student_no")
    private String studentNo;

    /**
     * 班级ID
     */
    @TableField("class_id")
    private Long classId;

    /**
     * 班级名称
     */
    @TableField("class_name")
    private String className;

    /**
     * 性别(1:男,2:女)
     */
    private Integer gender;

    /**
     * 出生日期
     */
    @TableField("birth_date")
    private Date birthDate;

    /**
     * 学生证
     */
    @TableField("education_no")
    private String educationNo;

    /**
     * 证件编号
     */
    @TableField("id_no")
    private String idNo;

    /**
     * 参加意向,1-可以参加;2-有限参加;3-暂停申报
     */
    private Integer intention;

    /**
     * 意向证明照片
     */
    @TableField("prove_img_url")
    private String proveImgUrl;

    /**
     * 疾病情况(严重/慢性疾病)
     */
    @TableField("serious_chronic_disease")
    private String seriousChronicDisease;

    /**
     * 过敏
     */
    private String allergy;

    /**
     * 是否治疗中
     */
    @TableField("is_treating")
    private String isTreating;

    /**
     * 是否曾因病入住医院
     */
    @TableField("is_hospitalized")
    private String isHospitalized;

    /**
     * 如遇意外之送往医院
     */
    @TableField("emergency_hospital")
    private Long emergencyHospital;

    /**
     * 医院名称
     */
    @TableField("emergency_hospital_name")
    private String emergencyHospitalName;

    /**
     * 医院医疗卡号码
     */
    @TableField("hospital_card_no")
    private String hospitalCardNo;

    /**
     * 金卡号码
     */
    @TableField("gold_card_no")
    private String goldCardNo;

    /**
     * 紧急联系人姓名1
     */
    @TableField("emergency_contact_name")
    private String emergencyContactName;

    /**
     * 紧急联系人手机号1
     */
    @TableField("emergency_contact_phone")
    private String emergencyContactPhone;

    /**
     * 紧急联系人姓名2
     */
    @TableField("emergency_contact_name_two")
    private String emergencyContactNameTwo;

    /**
     * 紧急联系人手机号2
     */
    @TableField("emergency_contact_phone_two")
    private String emergencyContactPhoneTwo;

    /**
     * 紧急联系人关系1
     */
    @TableField("emergency_contact_relation")
    private Integer emergencyContactRelation;

    /**
     * 紧急联系人关系2
     */
    @TableField("emergency_contact_relation_two")
    private Integer emergencyContactRelationTwo;

    /**
     * 本人与学生关系
     */
    @TableField("contact_relation")
    private String contactRelation;
}