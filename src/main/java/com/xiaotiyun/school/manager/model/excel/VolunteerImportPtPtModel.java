package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class VolunteerImportPtPtModel extends BasicImportModel {
    @ExcelProperty(value = "estudantes（Requerido）", index = 0)
    private String studentName;
    @ExcelProperty(value = "Número de estudante（Requerido）", index = 1)
    private String studentNo;
    @ExcelProperty(value = "Nome da atividade（Requerido）", index = 2)
    private String activityName;
    @ExcelProperty(value = "Nome da organização（Requerido）", index = 3)
    private String organization;
    @ExcelProperty(value = "Data de serviço（Requerido）", index = 4)
    private String serviceDate;
    @ExcelProperty(value = "Horas de serviço（Requerido）", index = 5)
    private String serviceHours;
}