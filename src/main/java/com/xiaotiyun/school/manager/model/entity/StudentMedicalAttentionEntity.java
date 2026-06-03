package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("student_medical_attention")
@ApiModel(description = "学生医护注意事项实体对象")
public class StudentMedicalAttentionEntity extends BaseEntity {
    @TableField("school_id")
    @ApiModelProperty(value = "学校ID", example = "1")
    private Long schoolId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @TableField("class_id")
    @ApiModelProperty(value = "班级ID", example = "1", required = true)
    private Long classId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @TableField("student_id")
    @ApiModelProperty(value = "学生ID", example = "12345", required = true)
    private Long studentId;

    @TableField("student_name")
    @ApiModelProperty(value = "学生姓名", example = "张三")
    private String studentName;

    @TableField("allergy")
    @ApiModelProperty(value = "过敏", example = "花生")
    private String allergy;

    @TableField("serious_chronic_disease")
    @ApiModelProperty(value = "严重/慢性疾病", example = "哮喘")
    private String seriousChronicDisease;

    @TableField("medical_notes")
    @ApiModelProperty(value = "医护备注", example = "注意休息")
    private String medicalNotes;

    @TableField("is_treating")
    @ApiModelProperty(value = "是否治疗中", example = "1")
    private String isTreating;

    @TableField("is_hospitalized")
    @ApiModelProperty(value = "是否住院", example = "1")
    private String isHospitalized;

}