package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class StudentAttendanceReportExportPtModel {
    @ExcelProperty("Número de Sala")
    private String seatNo;
    @ExcelProperty("Nome do Estudante")
    private String studentName;
    @ExcelProperty("Dias Esperados de Marcação")
    private String clockDays;
    @ExcelProperty("Dias Reais de Marcação")
    private String actualClockDays;
    @ExcelProperty("Número de Atrasos")
    private String beLateDays;
    @ExcelProperty("Número de Saídas Antecipadas")
    private String earlyDays;
    @ExcelProperty("Número de Faltas")
    private String leaveDays;
    @ExcelProperty("Dias de Serviço Público")
    private String businessDays;
    @ExcelProperty("Dias de Falta Sem Justificativa")
    private String notClockDays;
} 