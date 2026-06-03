package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ConventionalPerformancePtExportModel {
    @ExcelProperty("Período")
    private String termName;
    @ExcelProperty("Data do Evento")
    private String date;
    @ExcelProperty("Nome da Turma")
    private String className;
    @ExcelProperty("Nome do Estudante")
    private String studentName;
    @ExcelProperty("Tipo")
    private String type;
    @ExcelProperty("Frequência")
    private Integer frequency;
    @ExcelProperty("Hora de Criação")
    private String createTime;
    @ExcelProperty("Criador")
    private String createName;
}