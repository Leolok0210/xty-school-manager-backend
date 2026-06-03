package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class UserExportPtModel {
    @ExcelProperty("Número do Usuário")
    private String userNumber;
    @ExcelProperty("Nome de Login")
    private String loginName;
    @ExcelProperty("Nome do Usuário")
    private String username;
    @ExcelProperty("Número de Telefone")
    private String mobile;
    @ExcelProperty("Departamento")
    private String deptName;
    @ExcelProperty("Grupo de Usuários")
    private String userGroupName;
//    @ExcelProperty("Tipo de Usuário")
//    private String userType;
//    @ExcelProperty("Cargo")
//    private String position;
    @ExcelProperty("Gênero")
    private String gender;
//    @ExcelProperty("Estado")
//    private String status;
}