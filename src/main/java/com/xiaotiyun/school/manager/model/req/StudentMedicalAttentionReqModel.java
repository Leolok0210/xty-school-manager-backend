package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "学生医护注意事项请求模型")
public class StudentMedicalAttentionReqModel {

    @ApiModelProperty(value = "学校ID", example = "1")
    private Long schoolId;
//
//    @NotNull(message = LanguageConstants.PARAM_ERROR)
//    @ApiModelProperty(value = "学年", example = "2023-2024")
//    private String schoolYear;
//
//    @NotNull(message = LanguageConstants.PARAM_ERROR)
//    @ApiModelProperty(value = "班级ID", example = "1")
//    private Long classId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学生ID", example = "12345")
    private Long studentId;

//    @Size(max = 200, message = LanguageConstants.PARAM_ERROR)
//    @ApiModelProperty(value = "过敏", example = "花生")
//    private String allergy;
//
//    @Size(max = 200, message = LanguageConstants.PARAM_ERROR)
//    @ApiModelProperty(value = "严重/慢性疾病", example = "哮喘")
//    private String seriousChronicDisease;
//
//    @Size(max = 200, message = LanguageConstants.PARAM_ERROR)
//    @ApiModelProperty(value = "医护备注", example = "注意休息")
//    private String medicalNotes;
//
//    @ApiModelProperty(value = "是否治疗中", example = "1")
//    private String isTreating;
//
//    @ApiModelProperty(value = "是否住院", example = "1")
//    private String isHospitalized;
}