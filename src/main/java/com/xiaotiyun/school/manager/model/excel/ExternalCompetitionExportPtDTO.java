package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class ExternalCompetitionExportPtDTO {
    @ExcelProperty("Número de Identificação do Aluno")
    private String educationNo;
    @ExcelProperty("Número de Registro Escolar")
    private String studentNo;
    @ExcelProperty("Turma")
    private String className;
    @ExcelProperty("Nome do Aluno")
    private String studentName;
    @ExcelProperty("Número de Série")
    private String seatNo;
    @ExcelProperty("Categoria")
    private String categoryName;
    @ExcelProperty("Nome da Competição/Atividade")
    private String name;
    @ExcelProperty("Projeto/Grupo")
    private String groupName;
    @ExcelProperty("Prêmio")
    private String prize;
    @ExcelProperty("Nível do Prêmio")
    private String awardsName;
    @ExcelProperty("Organização")
    private String organizer;
    @ExcelProperty("Área da Atividade")
    private String activityArea;
    @ExcelProperty("Região")
    private String area;
    @ExcelProperty("Orientador")
    private String advisor;
    @ExcelProperty("Individual/Grupo")
    private String competitionType;
    @ExcelProperty("Data de Início da Atividade")
    private String startTime;
    @ExcelProperty("Data de Entrega do Prêmio")
    private String prizeTime;
    @ExcelProperty("É Representativo")
    private String representative;
    @ExcelProperty("Recomendação de Reconhecimento")
    private String awardsRemark;
    @ExcelProperty("Preenchedor")
    private String createUserName;
    @ExcelProperty("Maior Recomendação de Reconhecimento (Gerado Automaticamente)")
    private String autoAwardsRemark;
    @ExcelProperty("Reconhecimento Final")
    private String finalAwards;
    @ExcelProperty("Pontuação do Reconhecimento Final")
    private String finalAwardsPoints;
    @ExcelProperty("Observações da Revisão")
    private String approveRemark;
}
