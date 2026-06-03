package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class SubstituteRecordExportPtModel {
    @ExcelProperty("Data da Substituição")
    private String substituteDate;
    @ExcelProperty("Tipo de Substituição")
    private String substituteType;
    @ExcelProperty("Aula")
    private String lessonName;
    @ExcelProperty("Turma")
    private String className;
    @ExcelProperty("Disciplina")
    private String subjectName;
    @ExcelProperty("Professor Original")
    private String originalTeacherName;
    @ExcelProperty("Professor Substituto")
    private String substituteTeacherName;
    @ExcelProperty("Observações")
    private String remark;
}
