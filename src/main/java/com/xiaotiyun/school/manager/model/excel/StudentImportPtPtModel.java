package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class StudentImportPtPtModel extends BasicImportModel {
    @ExcelProperty(value = "Nome em Chinês (Obrigatório)", index = 0)
    private String chineseName;
    @ExcelProperty(value = "Número de Estudante (Obrigatório)", index = 1)
    private String studentNo;
    @ExcelProperty(value = "Número do Cartão de Estudante (Obrigatório)", index = 2)
    private String educationNo;
    @ExcelProperty(value = "Número do Lugar", index = 3)
    private String seatNo;
    @ExcelProperty(value = "Grupo de Nível(Obrigatório，Selecionar da lista)", index = 4)
    private String gradeGroup;
    @ExcelProperty(value = "Turma (obrigatório)", index = 5)
    private String className;
    @ExcelProperty(value = "Tipo de Estudante (Selecionar da lista)", index = 6)
    private String studentType;
    @ExcelProperty(value = "Nome em Língua Estrangeira", index = 7)
    private String englishName;
    @ExcelProperty(value = "Género（Selecionar da lista)", index = 8)
    private String gender;
    @ExcelProperty(value = "Data de Nascimento", index = 9)
    private String birthDate;
    @ExcelProperty(value = "Local de Nascimento（Selecionar da lista)", index = 10)
    private String birthPlace;
    @ExcelProperty(value = "Tipo de Documento（Selecionar da lista)", index = 11)
    private String idType;
    @ExcelProperty(value = "Número do Documento", index = 12)
    private String idNo;
    @ExcelProperty(value = "Local de Emissão do Documento（Selecionar da lista)", index = 13)
    private String idIssuePlace;
    @ExcelProperty(value = "Data de Emissão do Documento", index = 14)
    private String idIssueDate;
    @ExcelProperty(value = "Data de Validade do Documento", index = 15)
    private String idValidDate;
    @ExcelProperty(value = "Número do Permisso de Regresso", index = 16)
    private String reEntryPermitNo;
    @ExcelProperty(value = "Tipo de Autorização de Estadia（Selecionar da lista)", index = 17)
    private String stayType;
    @ExcelProperty(value = "Data de Emissão da Autorização", index = 18)
    private String stayIssueDate;
    @ExcelProperty(value = "Data de Validade da Autorização", index = 19)
    private String stayValidDate;
    @ExcelProperty(value = "Nacionalidade（Selecionar da lista)", index = 20)
    private String nationality;
    @ExcelProperty(value = "Origem Ancestral", index = 21)
    private String nativePlace;
    @ExcelProperty(value = "Telefone de Casa", index = 22)
    private String permanentPhone;
    @ExcelProperty(value = "Telemóvel", index = 23)
    private String mobilePhone;
    @ExcelProperty(value = "Morada Habitual - Distrito（Selecionar da lista)", index = 24)
    private String permanentAddressAreaId;
    @ExcelProperty(value = "Morada Habitual - Endereço Completo", index = 25)
    private String permanentAddress;
    @ExcelProperty(value = "Morada Noturna - Distrito（Selecionar da lista)", index = 26)
    private String nightAddressAreaId;
    @ExcelProperty(value = "Morada Noturna - Endereço Completo", index = 27)
    private String nightAddress;
    @ExcelProperty(value = "Nome do Tutor", index = 28)
    private String guardianName;
    @ExcelProperty(value = "Telefone de Contacto do Tutor", index = 29)
    private String guardianPhone;
    @ExcelProperty(value = "Telemóvel do Tutor", index = 30)
    private String guardianMobile;
    @ExcelProperty(value = "Profissão do Tutor", index = 31)
    private String guardianOccupation;
    @ExcelProperty(value = "Relação do Tutor com o Estudante（Selecionar da lista)", index = 32)
    private String guardianRelation;
    @ExcelProperty(value = "Morada do Tutor - Distrito（Selecionar da lista)", index = 33)
    private String guardianAddressAreaId;
    @ExcelProperty(value = "Morada do Tutor - Endereço Completo", index = 34)
    private String guardianAddress;
    @ExcelProperty(value = "Vive com o Tutor（Selecionar da lista)", index = 35)
    private String liveWithGuardian;
    @ExcelProperty(value = "Nome do Contacto de Emergência(Obrigatório)", index = 36)
    private String emergencyContact;
    @ExcelProperty(value = "Relação com o Estudante（Selecionar da lista)", index = 37)
    private String emergencyRelation;
    @ExcelProperty(value = "Telefone de Emergência (Obrigatório)", index = 38)
    private String emergencyPhone;
    @ExcelProperty(value = "Morada do Contacto de Emergência - Distrito（Selecionar da lista)", index = 39)
    private String emergencyAddressAreaId;
    @ExcelProperty(value = "Morada do Contacto de Emergência - Endereço Completo", index = 40)
    private String emergencyAddress;
    @ExcelProperty(value = "Conta WeCom do Estudante", index = 41)
    private String studentWeChat;
    @ExcelProperty(value = "Número de Telefone do Estudante", index = 42)
    private String studentPhone;
    @ExcelProperty(value = "Grau de Parentesco 1（Selecionar da lista)", index = 43)
    private String parentRelationOne;
    @ExcelProperty(value = "Número de Telefone do Responsável", index = 44)
    private String parentPhoneOne;
    @ExcelProperty(value = "Nome do Responsável", index = 45)
    private String parentName;
    @ExcelProperty(value = "Profissão do Responsável", index = 46)
    private String parentOccupation;
    @ExcelProperty(value = "Grau de Parentesco 2（Selecionar da lista)", index = 47)
    private String parentRelationTwo;
    @ExcelProperty(value = "Número de Telefone do Responsável", index = 48)
    private String parentPhoneTwo;
    @ExcelProperty(value = "Nome do Responsável", index = 49)
    private String parentNameTwo;
    @ExcelProperty(value = "Profissão do Responsável", index = 50)
    private String parentOccupationTwo;
    @ExcelProperty(value = "Grau de Parentesco 3（Selecionar da lista)", index = 51)
    private String parentRelationThree;
    @ExcelProperty(value = "Nome do Responsável", index = 52)
    private String parentNameThree;
    @ExcelProperty(value = "Número de Telefone do Responsável", index = 53)
    private String parentPhoneThree;
    @ExcelProperty(value = "Grau de Parentesco 4（Selecionar da lista)", index = 54)
    private String parentRelationFour;
    @ExcelProperty(value = "Nome do Responsável", index = 55)
    private String parentNameFour;
    @ExcelProperty(value = "Número de Telefone do Responsável", index = 56)
    private String parentPhoneFour;
}