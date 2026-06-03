package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class UserImportHeaderPtModel {
    @ExcelProperty("Nome de usuário (Obrigatório)")
    private String uName;
    @ExcelProperty("País/Região do Número")
    private String phoneArea;
    @ExcelProperty("Número de Celular")
    private String phone;
    @ExcelProperty("Nome Completo (Obrigatório)")
    private String userName;
    @ExcelProperty("Departamento (Obrigatório)")
    private String deptName;
    @ExcelProperty("Grupo de Usuário (Obrigatório)")
    private String userGroup;
    @ExcelProperty("ID do Usuário (Obrigatório)")
    private String userNumber;
//    @ExcelProperty("Cargo (Obrigatório)")
//    private String position;
    @ExcelProperty("Gênero (Obrigatório)")
    private String gender;
}