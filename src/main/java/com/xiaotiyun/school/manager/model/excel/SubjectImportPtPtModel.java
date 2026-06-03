package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class SubjectImportPtPtModel {

        @ExcelProperty("Código da Disciplina (obrigatório)")
        private String subjectNumber;

        @ExcelProperty("Nome da Disciplina (obrigatório)")
        private String subjectName;

        @ExcelProperty("Nome em Inglês")
        private String subjectEnglishName;

        @ExcelProperty("Unidade\n" +
                " (Formato: número, 1-100)")
        private String unit;
        @ExcelProperty("Departamento Aplicável(obrigatório)\n" +
                "Entradas suportadas: Jardim de Infância, Escola Primária, Escola Secundária\n" +
                "Para múltiplos valores: separar com vírgula \",\"")
        private String scope;

        private Integer rowIndex;
}