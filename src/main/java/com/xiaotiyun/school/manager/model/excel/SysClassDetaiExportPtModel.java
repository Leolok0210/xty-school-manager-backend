package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SysClassDetaiExportPtModel {
    @ExcelIgnore
    private Long id;
    @ExcelProperty(value = "Ano Acadêmico", index = 0)
    @ApiModelProperty(value = "學年")
    private String sid;

    @ExcelProperty(value = "ID da Turma", index = 1)
    @ApiModelProperty(value = "班級編號")
    private String classNumber;

    @ExcelProperty(value = "Nome da Turma", index = 2)
    @ApiModelProperty(value = "班級名稱")
    private String className;

    @ExcelProperty(value = "Número da Turma", index = 3)
    @ApiModelProperty(value = "班級序號")
    private Integer classSerialNumber;

    @ExcelProperty(value = "Grupo de Série", index = 4)
    @ApiModelProperty(value = "級組")
    private String gradeGroupName;

    @ExcelProperty(value = "É Classe Especializada", index = 5)
    @ApiModelProperty(value = "是否專業班")
    private String professionalVersionName;

    @ExcelProperty(value = "Área", index = 6)
    @ApiModelProperty(value = "文/理科")
    private String artsScienceName;

    @ExcelProperty(value = "Nome do Curso", index = 7)
    @ApiModelProperty(value = "專業名稱")
    private String professionalName;

    @ExcelProperty(value = "Professor Responsável", index = 8)
    @ApiModelProperty(value = "班主任")
    private String headTeacherName;
    @ExcelIgnore
    private Integer artsScience;
    @ExcelIgnore
    private Integer professionalVersion;
    @ExcelIgnore
    private Integer department;
    @ExcelIgnore
    private Long gradeGroup;
    @ExcelIgnore
    private Long professionalId;
    @ExcelIgnore
    private Long headTeacher;
    @ExcelIgnore
    private Long schoolId;
}
