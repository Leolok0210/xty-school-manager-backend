package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class SysClassPtImportModel {


    @ExcelProperty("Grupo de Nível (obrigatório，Selecionar da lista)")
    private String gradeGroup;

    @ExcelProperty("Número da Turma (obrigatório)")
    private String classSerialNumber;

    @ExcelProperty("Nome da Turma")
    private String className;

//    @ExcelProperty("É Turma Especializada (obrigatório)")
//    private String professionalVersion;

    @ExcelProperty("Área（Selecionar da lista)")
    private String artsScience;

    @ExcelProperty("Nome do Curso")
    private String professional;

    @ExcelProperty("Número de Utilizador do Professor Titular (obrigatório)")
    private String headTeacher;


    private Integer rowIndex;
}