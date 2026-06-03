package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class TeacherLeaveReportExportPtModel {
    @ExcelProperty("Nome")
    private String teacherName;

    @ExcelProperty("Cargo")
    private String teacherPosition;

    @ExcelProperty("Número")
    private String teacherNumber;

    @ExcelProperty("Dias de Presença Obrigatória")
    private Integer shouldAttendanceDays;

    @ExcelProperty("Dias de Presença Real")
    private Integer actualAttendanceDays;

    @ExcelProperty("Número de Atrasos")
    private Integer lateCount;

    @ExcelProperty("Número de Saídas Antecipadas")
    private Integer earlyCount;

    @ExcelProperty("Número de Licenças")
    private Integer leaveCount;

    @ExcelProperty("Número de Serviços Oficiais")
    private Integer officialCount;

    @ExcelProperty("Número de Ausências sem Justificação")
    private Integer noReasonCount;
}
