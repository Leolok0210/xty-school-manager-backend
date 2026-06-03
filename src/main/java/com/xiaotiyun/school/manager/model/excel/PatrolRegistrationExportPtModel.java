package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class PatrolRegistrationExportPtModel {
    @ExcelProperty("Ano lectivo de")
    private String schoolYear;

    @ExcelProperty("Aprender algum")
    private String semesterName;

    @ExcelProperty("Nome da classe")
    private String className;

    @ExcelProperty("Classe não.")
    private Long studentClassNumber;

    @ExcelProperty("Nome do aluno")
    private String studentName;

    @ExcelProperty("data")
    private String registrationDate;

    @ExcelProperty("classe")
    private String lessonPeriod;

    @ExcelProperty("Detalhes do conteúdo do registro")
    private String registrationContent;

    @ExcelProperty("Pessoa que registrou")
    private String registrant;
}
