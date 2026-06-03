package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class MedicalRecordExportPtModel {

    @ExcelProperty("classes")
    private String className;

    @ExcelProperty("Nome do aluno")
    private String studentName;

    @ExcelProperty("sexo")
    private String genderStr;

    @ExcelProperty("Alergias")
    private String allergy;

    @ExcelProperty("Doenças graves/crónicas")
    private String seriousChronicDisease;

    @ExcelProperty("Observações de cuidados médicos")
    private String medicalNotes;

    @ExcelProperty("Hospital a ser levado em caso de acidente")
    private String hospital;

    @ExcelProperty("Queixa história atual")
    private String chiefComplaintAll;

    @ExcelProperty("transformação")
    private String treatment;

    @ExcelProperty("observações")
    private String notes;

    @ExcelProperty("Horário de consulta")
    private String consultationDate;
}
