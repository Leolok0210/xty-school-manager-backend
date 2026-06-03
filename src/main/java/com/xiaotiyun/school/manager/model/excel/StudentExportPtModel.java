package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class StudentExportPtModel {
    @ExcelProperty("Ano Escolar") // 學年
    private String schoolYear;
    @ExcelProperty("Nome do Aluno") // 學生姓名
    private String studentName;
    @ExcelProperty("Gênero") // 性別
    private String gender;
    @ExcelProperty("Número do Aluno") // 學生編號
    private String studentNo;
    @ExcelProperty("Número do Assento") // 座位號
    private String seatNo;
    @ExcelProperty("Nome da Turma") // 班級名稱
    private String className;
    @ExcelProperty("Grupo de Nível") // 級組
    private String gradeName;
    @ExcelProperty("Status") // 狀態
    private String status;
    @ExcelProperty("Nome em Inglês") // 英文名
    private String englishName;
    @ExcelProperty("Número da DSEJ") // 教青局編號
    private String educationNo;
    @ExcelProperty("Nacionalidade") // 國籍
    private String nationality;
    @ExcelProperty("Local de Origem") // 籍貫
    private String nativePlace;
    @ExcelProperty("Tipo de Documento") // 證件類型
    private String idType;
    @ExcelProperty("Número do Documento") // 證件編號
    private String idNo;
    @ExcelProperty("Endereço Permanente") // 常住地址
    private String permanentAddress;
    @ExcelProperty("Telemóvel") // 手提電話
    private String mobilePhone;
    @ExcelProperty("Telefone do Endereço Permanente") // 住址電話
    private String permanentPhone;
    @ExcelProperty("Data de Desistência") // 退学日期
    private String outTime;
    @ExcelProperty("Motivo da Desistência") // 退学原因
    private String outReason;
    @ExcelProperty("Situação de Promoção/Retenção") // 升留级情况
    private String escalationSituation;
} 