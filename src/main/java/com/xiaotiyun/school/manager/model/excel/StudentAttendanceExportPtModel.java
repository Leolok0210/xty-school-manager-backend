package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class StudentAttendanceExportPtModel {
    @ExcelProperty("Data")
    private String attendanceDate;
    @ExcelProperty("ID do Estudante")
    private String studentNo;
    @ExcelProperty("Classe")
    private String className;
    @ExcelProperty("Nome do Estudante")
    private String studentName;
    @ExcelProperty("Hora de Entrada (Manhã)")
    private String morningInTime;
    @ExcelProperty("Hora de Saída (Manhã)")
    private String morningOutTime;
    @ExcelProperty("Hora de Entrada (Tarde)")
    private String afternoonInTime;
    @ExcelProperty("Hora de Saída (Tarde)")
    private String afternoonOutTime;
    @ExcelProperty("Status")
    private String status;
    @ExcelProperty("Observação")
    private String remark;
}
