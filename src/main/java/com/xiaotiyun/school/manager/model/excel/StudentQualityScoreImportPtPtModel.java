package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class StudentQualityScoreImportPtPtModel {
    @ExcelProperty("Número do Assento")
    private String seatNumber;
    @ExcelProperty("Nome Chinês")
    private String chineseName;
    @ExcelProperty("Número do Estudante")
    private String studentNumber;
    @ExcelProperty("Projeto de Qualidade")
    private String qualityProject;
    @ExcelProperty("Pontuação")
    private String qualityProjectScore;
    @ExcelProperty("Departamento")
    private String department;
    private Integer rowIndex;
}