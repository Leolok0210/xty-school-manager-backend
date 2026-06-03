package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UserRewardImportModel extends BasicImportModel {
    @ExcelProperty("学生姓名")
    private String studentName;
    @ExcelProperty("学生编号")
    private String studentCode;
    @ExcelProperty("会议通过日期")
    private String meetingDate;
    @ExcelProperty("原因")
    private String rewardReason;
    @ExcelProperty("类型")
    private String rewardType;
    @ExcelProperty("次数")
    private String frequency;
    @ExcelProperty("备注")
    private String remark;
}