package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class TeacherBusinessExportPtModel {
    @ExcelProperty("Nome do Professor")
    private String teacherName;
    @ExcelProperty("Data e Hora de Início")
    private String startTime;
    @ExcelProperty("Data e Hora de Término")
    private String endTime;
    @ExcelProperty("Motivo")
    private String reason;
}
