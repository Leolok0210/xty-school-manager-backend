package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class TeacherAttendanceImportPtPtModel extends BasicImportModel {
    @ExcelProperty(value = "Número de Utilizador do Professor (obrigatório)", index = 0)
    private String userNumber;
    @ExcelProperty(value = "Nome do Professor (obrigatório)", index = 1)
    private String teacherName;
    @ExcelProperty(value = "Número do Cartão", index = 2)
    private String cardNumber;
    @ExcelProperty(value = "Data (obrigatório)", index = 3)
    private String attendanceDate;
    @ExcelProperty(value = "Hora (obrigatório)", index = 4)
    private String attendanceTime;
} 