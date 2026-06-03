package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@ApiModel(description = "学生医护注意事项响应模型")
@AllArgsConstructor
@NoArgsConstructor
public class StudentMedicalAttentionResModel {
    @ApiModelProperty(value = "主键ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "班级ID", example = "1")
    private Long classId;

    @ApiModelProperty(value = "学生ID", example = "12345")
    private Long studentId;

    @ApiModelProperty(value = "学生姓名", example = "张三")
    private String studentName;

    @ApiModelProperty(value = "过敏", example = "花生")
    private String allergy;

    @ApiModelProperty(value = "严重/慢性疾病", example = "哮喘")
    private String seriousChronicDisease;

    @ApiModelProperty(value = "医护备注", example = "注意休息")
    private String medicalNotes;

    @ApiModelProperty(value = "是否治疗中", example = "1")
    private String isTreating;

    @ApiModelProperty(value = "是否住院", example = "1")
    private String isHospitalized;
}