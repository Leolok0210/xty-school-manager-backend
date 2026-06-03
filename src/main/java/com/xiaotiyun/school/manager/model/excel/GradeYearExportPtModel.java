package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class GradeYearExportPtModel {

    @ExcelProperty("Nome do assunto")
    private String subjectName;

    @ExcelProperty("Número de pessoas elegíveis")
    private Integer qualifiedCount;

    @ExcelProperty("Taxa de aprovação")
    private String qualifiedRate;

    @ExcelProperty("Número de pessoas não qualificadas")
    private Integer flunkCount;

    @ExcelProperty("Taxa de rejeição")
    private String flunkRate;

    @ExcelProperty("60-80 O número de")
    private Integer sixtyToEightyCount;

    @ExcelProperty("60-80 taxa")
    private String sixtyToEightyRate;

    @ExcelProperty("80-90 O número de")
    private Integer eightyToNinetyCount;

    @ExcelProperty("80 90 taxa")
    private String eightyToNinetyRate;

    @ExcelProperty("90-100 O número de")
    private Integer ninetyToHundredCount;

    @ExcelProperty("90-100 taxa")
    private String ninetyToHundredRate;
}
