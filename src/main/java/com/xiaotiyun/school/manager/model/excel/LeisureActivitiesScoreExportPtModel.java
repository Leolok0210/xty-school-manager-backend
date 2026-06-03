package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class LeisureActivitiesScoreExportPtModel {
    @ExcelProperty("Turma")
    private String className;
    @ExcelProperty("Número na turma")
    private String seatNo;
    @ExcelProperty("Nome")
    private String studentName;
    @ExcelProperty("Número do Aluno")
    private String studentNo;
    @ExcelProperty("Disciplina")
    private String courseName;
    @ExcelProperty("Número de Presenças")
    private String attendCount;
    @ExcelProperty("Pontuação de Frequência")
    private String attendScore;
    @ExcelProperty("Pontuação de Desempenho")
    private String lessonScore;
    @ExcelProperty("Pontuação Total")
    private String totalScore;
    @ExcelProperty("Nível de Referência")
    private String referenceLevel;
    @ExcelProperty("Nível Final")
    private String finalLevel;
} 