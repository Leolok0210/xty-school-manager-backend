package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class StudentPromotionExportPtModel {
    @ExcelProperty("Ano escolar")
    private String schoolYear;
    @ExcelProperty("Nome da turma")
    private String className;
    @ExcelProperty("Número na turma")
    private String seatNo;
    @ExcelProperty("Nome do aluno")
    private String studentName;
    @ExcelProperty("Promoção/Repetição")
    private String promotionType;
}