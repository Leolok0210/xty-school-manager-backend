package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class StudentLeaveExportPtModel {
    @ExcelProperty("Ano Acadêmico")
    private String schoolYear;
    @ExcelProperty("Nome da Turma")
    private String className;
    @ExcelProperty("Número de Assento")
    private String seatNo;
    @ExcelProperty("Nome do Estudante")
    private String studentName;
    @ExcelProperty("Data")
    private String leaveDate;
    @ExcelProperty("Tipo")
    private String leaveType;
    @ExcelProperty("Número de Aulas")
    private String periods;
    @ExcelProperty("Observação")
    private String remark;
}
