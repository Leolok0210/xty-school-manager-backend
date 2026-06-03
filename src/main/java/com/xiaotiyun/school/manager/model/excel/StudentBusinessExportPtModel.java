package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class StudentBusinessExportPtModel {
    @ExcelProperty("Ano Letivo")
    private String schoolYear;
    @ExcelProperty("Nome da Turma")
    private String className;
    @ExcelProperty("Nome do Estudante")
    private String studentName;
    @ExcelProperty("Data de Início")
    private String startTime;
    @ExcelProperty("Data de Fim")
    private String endTime;
    @ExcelProperty("Motivo")
    private String reason;
}
