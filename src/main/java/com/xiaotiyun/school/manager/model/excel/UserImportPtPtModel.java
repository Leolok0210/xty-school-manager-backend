package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UserImportPtPtModel extends BasicImportModel {
    @ExcelProperty(value = "Nome de usuário (Obrigatório)", index = 0)
    private String uName;
    @ExcelProperty(value = "País/Região do Número", index = 1)
    private String phoneArea;
    @ExcelProperty(value = "Número de Celular", index = 2)
    private String phone;
    @ExcelProperty(value = "Nome Completo (Obrigatório)", index = 3)
    private String userName;
    @ExcelProperty(value = "Departamento (Obrigatório)", index = 4)
    private String deptName;
    @ExcelProperty(value = "Grupo de Usuário (Obrigatório)", index = 5)
    private String userGroup;
    @ExcelProperty(value = "ID do Usuário (Obrigatório)", index = 6)
    private String userNumber;
//    @ExcelProperty(value = "Cargo (Obrigatório)", index = 6)
//    private String position;
    @ExcelProperty(value = "Gênero (Obrigatório)", index = 7)
    private String gender;
}