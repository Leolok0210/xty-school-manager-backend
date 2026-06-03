package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class GradeClassAvgExportPtModel {
    @ExcelProperty("Nome da classe")
    private String className;

    @ExcelProperty("Média de pontuação")
    private String averageScore;

    @ExcelProperty("Tamanho da turma")
    private Integer classSize;
}
