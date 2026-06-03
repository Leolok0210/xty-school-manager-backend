package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class TeacherBusinessExportModel {
    @ExcelProperty("老師姓名")
    private String teacherName;
    @ExcelProperty("開始日期")
    private String startTime;
    @ExcelProperty("結束時間")
    private String endTime;
    @ExcelProperty("事由")
    private String reason;
} 