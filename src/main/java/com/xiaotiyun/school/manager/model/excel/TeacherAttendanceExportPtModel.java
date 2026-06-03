package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class TeacherAttendanceExportPtModel {
    @ExcelProperty("Data")
    private String attendanceDate;
    @ExcelProperty("ID do Professor")
    private String teacherNumber;
    @ExcelProperty("Nome do Professor")
    private String teacherName;
    @ExcelProperty("Número do Cartão")
    private String cardNumber;
    @ExcelProperty("Hora de Entrada")
    private String clockInTime;
    @ExcelProperty("Hora de Saída")
    private String clockOutTime;
    @ExcelProperty("Status")
    private String status;
    @ExcelProperty("Observação")
    private String remark;
}
