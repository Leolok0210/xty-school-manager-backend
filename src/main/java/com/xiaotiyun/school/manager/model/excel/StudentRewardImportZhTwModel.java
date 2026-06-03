package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class StudentRewardImportZhTwModel extends BasicImportModel {
    @ExcelProperty(value = "學生中文姓名（必填）", index = 0)
    private String studentName;
    @ExcelProperty(value = "學生編號（必填）", index = 1)
    private String studentCode;
    @ExcelProperty(value = "會議通過日期（必填）", index = 2)
    private String meetingDate;
    @ExcelProperty(value = "原因（必填）", index = 3)
    private String rewardReason;
    @ExcelProperty(value = "類型（必填）\n" +
            "可輸入：大功、小功、優點", index = 4)
    private String rewardType;
    @ExcelProperty(value = "次數（必填）", index = 5)
    private String frequency;
    @ExcelProperty(value = "備註", index = 6)
    private String remark;
}