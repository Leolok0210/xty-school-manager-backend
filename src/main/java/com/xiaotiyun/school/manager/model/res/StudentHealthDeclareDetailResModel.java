package com.xiaotiyun.school.manager.model.res;

import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 学生健康申报表实体类
 * @author generated
 * @since 2023-09-01
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("学生健康申报表详情")
public class StudentHealthDeclareDetailResModel extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 学年
     */
    @ApiModelProperty(value = "学年")
    private String schoolYear;

    /**
     * 学校ID
     */
    @ApiModelProperty(value = "学校ID")
    private Long schoolId;

    /**
     * 学生ID
     */
    @ApiModelProperty(value = "学生ID")
    private Long studentId;

    /**
     * 学生名称
     */
    @ApiModelProperty(value = "学生名称")
    private String studentName;

    /**
     * 学生学号
     */
    @ApiModelProperty(value = "学生学号")
    private String studentNo;

    /**
     * 班级ID
     */
    @ApiModelProperty(value = "班级ID")
    private Long classId;

    /**
     * 班级名称
     */
    @ApiModelProperty(value = "班级名称")
    private String className;

    /**
     * 性别(1:男,2:女)
     */
    @ApiModelProperty(value = "性别(1:男,2:女)")
    private Integer gender;

    /**
     * 出生日期
     */
    @ApiModelProperty(value = "出生日期")
    private Date birthDate;

    /**
     * 学生证
     */
    @ApiModelProperty(value = "学生证")
    private String educationNo;

    /**
     * 证件编号
     */
    @ApiModelProperty(value = "证件编号")
    private String idNo;

    /**
     * 参加意向,1-可以参加;2-有限参加;3-暂停申报
     */
    @ApiModelProperty(value = "参加意向,1-可以参加;2-有限参加;3-暂停申报")
    private Integer intention;

    /**
     * 意向证明照片
     */
    @ApiModelProperty(value = "意向证明照片")
    private String proveImgUrl;

    /**
     * 疾病情况(严重/慢性疾病)
     */
    @ApiModelProperty(value = "疾病情况(严重/慢性疾病)")
    private String seriousChronicDisease;

    /**
     * 过敏
     */
    @ApiModelProperty(value = "过敏")
    private String allergy;

    /**
     * 是否治疗中
     */
    @ApiModelProperty(value = "是否治疗中")
    private String isTreating;

    /**
     * 是否曾因病入住医院
     */
    @ApiModelProperty(value = "是否曾因病入住医院")
    private String isHospitalized;

    /**
     * 如遇意外之送往医院
     */
    @ApiModelProperty(value = "如遇意外之送往医院")
    private Long emergencyHospital;

    /**
     * 医院名称
     */
    @ApiModelProperty(value = "医院名称")
    private String emergencyHospitalName;

    /**
     * 医院医疗卡号码
     */
    @ApiModelProperty(value = "医院医疗卡号码")
    private String hospitalCardNo;

    /**
     * 金卡号码
     */
    @ApiModelProperty(value = "金卡号码")
    private String goldCardNo;

    /**
     * 紧急联系人姓名1
     */
    @ApiModelProperty(value = "紧急联系人姓名1")
    private String emergencyContactName;

    /**
     * 紧急联系人手机号1
     */
    @ApiModelProperty(value = "紧急联系人手机号1")
    private String emergencyContactPhone;

    /**
     * 紧急联系人关系1
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "紧急联系人关系1")
    private Integer emergencyContactRelation;

    /**
     * 紧急联系人姓名2
     */
    @ApiModelProperty(value = "紧急联系人姓名2")
    private String emergencyContactNameTwo;

    /**
     * 紧急联系人手机号2
     */
    @ApiModelProperty(value = "紧急联系人手机号2")
    private String emergencyContactPhoneTwo;

    /**
     * 紧急联系人关系2
     */
    @ApiModelProperty(value = "紧急联系人关系2")
    private Integer emergencyContactRelationTwo;

    /**
     * 本人与学生关系
     */
    @ApiModelProperty(value = "本人与学生关系")
    private String contactRelation;
}
