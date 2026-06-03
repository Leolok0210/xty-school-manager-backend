package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class BigLittleRestExportPtModel {
    @ExcelProperty("Ano lectivo de")
    private String schoolYear;

    @ExcelProperty("Aprenda o nome do segmento")
    private String semesterName;

    @ExcelProperty("Nome da classe")
    private String className;

    @ExcelProperty("Nome do aluno")
    private String studentName;

    @ExcelProperty("data")
    private String registrationDate;

    @ExcelProperty("Cupão grande/cupão pequeno")
    private String type;

    @ExcelProperty("Desempenho com fôrma grande e fôrma pequena")
    private String registrationContent;

    @ExcelProperty("Pessoa que registrou")
    private String registrant;
}
