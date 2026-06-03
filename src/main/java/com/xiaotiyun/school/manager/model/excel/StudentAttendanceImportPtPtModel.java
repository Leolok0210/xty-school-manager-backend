package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class StudentAttendanceImportPtPtModel extends BasicImportModel {
    @ExcelProperty(value = "Número do Estudantis (obrigatório)", index = 0)
    private String studentNo;
    @ExcelProperty(value = "Nome em Chinês (obrigatório)", index = 1)
    private String studentName;
    @ExcelProperty(value = "Tipo (obrigatório)", index = 2)
    private String type;
    @ExcelProperty(value = "Data (obrigatório)", index = 3)
    private String attendanceDate;
    @ExcelProperty(value = "Hora (obrigatório)", index = 4)
    private String attendanceTime;
} 