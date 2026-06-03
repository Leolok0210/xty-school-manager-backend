package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class StudentMedicalRecordImportPtModel {
    /**
     * Nome em Chinês do Aluno (Obrigatório)
     */
    @ExcelProperty("Nome em Chinês do Aluno (Obrigatório)")
    private String studentName;

    /**
     * Número do Aluno (Obrigatório)
     */
    @ExcelProperty("Número do Aluno (Obrigatório)")
    private String studentNumber;

    /**
     * Data da Consulta (Obrigatório)
     */
    @ExcelProperty("Data da Consulta (Obrigatório)")
    private String consultationDate;

    /**
     * Hora da Consulta (Obrigatório)
     */
    @ExcelProperty("Hora da Consulta (Obrigatório)")
    private String consultationTime;

    /**
     * Tratamento (Obrigatório)
     */
    @ExcelProperty("Tratamento (Obrigatório)")
    private String treatment;

    /**
     * Observações (Obrigatório)
     */
    @ExcelProperty("Observações (Obrigatório)")
    private String notes;

    /**
     * Temperatura
     */
    @ExcelProperty("Temperatura")
    private String temperature;

    /**
     * Febre
     */
    @ExcelProperty("Febre")
    private String fever;

    /**
     * Tosse
     */
    @ExcelProperty("Tosse")
    private String cough;

    /**
     * Corrimento Nasal
     */
    @ExcelProperty("Corrimento Nasal")
    private String runnyNose;

    /**
     * Dor de Garganta
     */
    @ExcelProperty("Dor de Garganta")
    private String soreThroat;

    /**
     * Tonturas
     */
    @ExcelProperty("Tonturas")
    private String dizziness;

    /**
     * Dor de Cabeça
     */
    @ExcelProperty("Dor de Cabeça")
    private String headache;

    /**
     * Hemorragia Nasal
     */
    @ExcelProperty("Hemorragia Nasal")
    private String nosebleed;

    /**
     * Náuseas
     */
    @ExcelProperty("Náuseas")
    private String nausea;

    /**
     * Frequência de Vómitos
     */
    @ExcelProperty("Frequência de Vómitos")
    private String vomitingCount;

    /**
     * dor abdominal
     */
    @ExcelProperty("Dor Abdominal")
    private String abdominalPain;

    /**
     * Frequência de Diarreia
     */
    @ExcelProperty("Frequência de Diarreia")
    private String diarrheaCount;

    /**
     * Outros Sintomas
     */
    @ExcelProperty("Outros Sintomas")
    private String chiefComplaint;

    private Integer excelLineNo;
}