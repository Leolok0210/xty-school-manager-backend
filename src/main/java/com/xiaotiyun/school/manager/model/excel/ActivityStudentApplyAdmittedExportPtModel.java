package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ActivityStudentApplyAdmittedExportPtModel {
    @ExcelProperty("Turma")
    private String className;
    @ExcelProperty("Número na turma")
    private String seatNo;
    @ExcelProperty("Nome")
    private String studentName;
    @ExcelProperty("Número do Aluno")
    private String studentNo;
    @ExcelProperty("Curso Correspondente")
    private String courseName;
    @ExcelProperty("Fase de Admissão")
    private String admissionStage;
    @ExcelProperty("Método de Admissão")
    private String admissionMethod;
}