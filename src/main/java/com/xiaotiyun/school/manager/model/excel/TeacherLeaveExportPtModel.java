package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class TeacherLeaveExportPtModel {
    @ExcelProperty("Professor de Licença")
    private String teacherName;
    @ExcelProperty("Tipo de Licença")
    private String leaveType;
    @ExcelProperty("Data e Hora de Início")
    private String startTime;
    @ExcelProperty("Data e Hora de Término")
    private String endTime;
    @ExcelProperty("Motivo da Licença")
    private String reason;
}
