package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class VolunteerExportPtModel {
    @ExcelProperty("Ano Acadêmico")
    private String schoolYear;
    @ExcelProperty("Nome da Turma")
    private String className;
    @ExcelProperty("Nome do Estudante")
    private String studentName;
    @ExcelProperty("Número de Assento")
    private String seatNo;
    @ExcelProperty("Nome da Atividade")
    private String activityName;
    @ExcelProperty("Nome da Organização")
    private String organization;
    @ExcelProperty("Data de Serviço")
    private String serviceDate;
    @ExcelProperty("Hora de Início")
    private String startTime;
    @ExcelProperty("Hora de Término")
    private String endTime;
    @ExcelProperty("Horas de Serviço")
    private String serviceSeconds;
    @ExcelProperty("Natureza do Serviço")
    private String serviceNature;
    @ExcelProperty("Categoria de Serviço")
    private String serviceType;
}
