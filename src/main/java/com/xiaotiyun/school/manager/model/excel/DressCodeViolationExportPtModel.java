package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class DressCodeViolationExportPtModel {
    @ExcelProperty("Ano lectivo de")
    private String schoolYear;

    @ExcelProperty("Aprenda o nome do segmento")
    private String semesterName;

    @ExcelProperty("Nome da classe")
    private String className;

    @ExcelProperty("Classe não.")
    private String studentClassNumber;

    @ExcelProperty("Nome do aluno")
    private String studentName;

    @ExcelProperty("data")
    private String violationDate;

    @ExcelProperty("observações")
    private String remark;

    @ExcelProperty("Pessoa que registrou")
    private String registrant;
}
