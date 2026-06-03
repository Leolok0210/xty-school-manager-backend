package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ConventionalPerformanceImportPtPtModel extends BasicImportModel {
    @ExcelProperty(value = "Nome em Chinês do Aluno (obrigatório)", index = 0)
    private String studentName;
    @ExcelProperty(value = "Número do Aluno  (obrigatório)", index = 1)
    private String studentCode;
    @ExcelProperty(value = "Data do Incidente (obrigatório)", index = 2)
    private String date;
    @ExcelProperty(value = "Tarefas em Falta (obrigatório)\n" +
            "Introduza 0 se não houver infração", index = 3)
    private String missingHomework;
    @ExcelProperty(value = "Livro em Falta (obrigatório)\n" +
            "Introduza 0 se não houver infração", index = 4)
    private String missingTextbook;
    @ExcelProperty(value = "Infração em Sala de Aula (obrigatório)\n" +
            "Introduza 0 se não houver infração", index = 5)
    private String classViolation;
    @ExcelProperty(value = "Aparência Impropria (obrigatório)\n" +
            "Introduza 0 se não houver infração", index = 6)
    private String uniformNonCompliance;
    @ExcelProperty(value = "Comprovativo em Falta (obrigatório)\n" +
            "Introduza 0 se não houver infração", index = 7)
    private String missingReturnSticker;
    @ExcelProperty(value = "Tarefas em Falta", index = 8)
    private String missingHomeworkRemark;
    @ExcelProperty(value = "Livro em Falta", index = 9)
    private String missingTextbookRemark;
    @ExcelProperty(value = "Infração em Sala de Aula", index = 10)
    private String classViolationRemark;
    @ExcelProperty(value = "Aparência Impropria", index = 11)
    private String uniformNonComplianceRemark;
    @ExcelProperty(value = "Comprovativo em Falta", index = 12)
    private String missingReturnStickerRemark;
}