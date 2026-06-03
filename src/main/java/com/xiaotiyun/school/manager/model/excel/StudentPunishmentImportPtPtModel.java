package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class StudentPunishmentImportPtPtModel extends BasicImportModel {
    @ExcelProperty(value = "Nome em Chinês do Aluno (obrigatório)", index = 0)
    private String studentName;
    @ExcelProperty(value = "Número do Aluno  (obrigatório)", index = 1)
    private String studentCode;
    @ExcelProperty(value = "Data de Aprovação em Reunião (obrigatório)", index = 2)
    private String meetingDate;
    @ExcelProperty(value = "Motivo (obrigatório)", index = 3)
    private String rewardReason;
    @ExcelProperty(value = "Tipo (obrigatório)\n" +
            "Introduzível：Falta Grave, Falta Leve, Ponto Disciplinar", index = 4)
    private String rewardType;
    @ExcelProperty(value = "Número de Vezes (obrigatório)", index = 5)
    private String frequency;
    @ExcelProperty(value = "Observações", index = 6)
    private String remark;
}