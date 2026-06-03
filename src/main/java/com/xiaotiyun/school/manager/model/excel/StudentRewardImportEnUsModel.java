package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class StudentRewardImportEnUsModel extends BasicImportModel {
    @ExcelProperty(value = "Student Chinese Name(required)", index = 0)
    private String studentName;
    @ExcelProperty(value = "Student ID(required)", index = 1)
    private String studentCode;
    @ExcelProperty(value = "Approval Date in Meeting(required)", index = 2)
    private String meetingDate;
    @ExcelProperty(value = "Reason(required)", index = 3)
    private String rewardReason;
    @ExcelProperty(value = "Type(required)\n" +
            "Enterable：Major Merit, Minor Merit, Commendation", index = 4)
    private String rewardType;
    @ExcelProperty(value = "Number of Times (required)", index = 5)
    private String frequency;
    @ExcelProperty(value = "Remarks", index = 6)
    private String remark;
}