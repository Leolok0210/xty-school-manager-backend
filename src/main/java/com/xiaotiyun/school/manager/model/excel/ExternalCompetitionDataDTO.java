package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ExternalCompetitionDataDTO {
    @ExcelProperty("學生證編號")
    private String educationNo;
    @ExcelProperty("校本學生編號")
    private String studentNo;
    @ExcelProperty("班別")
    private String className;
    @ExcelProperty("学号")
    private String seatNo;
    @ExcelProperty("學生姓名")
    private String studentName;
    @ExcelProperty("表現範疇")
    private String categoryName;
    @ExcelProperty("比赛活动名称")
    private String name;
    @ExcelProperty("项目/組別")
    private String groupName;
    @ExcelProperty("獎項")
    private String prize;
    @ExcelProperty("奬項評級")
    private String awardsName;
    @ExcelProperty("主辦單位")
    private String organizer;
    @ExcelProperty("活动地区")
    private String activityArea;
    @ExcelProperty("地區")
    private String area;
    @ExcelProperty("指導老師")
    private String advisor;
    @ExcelProperty("個人/團體")
    private String competitionType;
    @ExcelProperty("活動開始日期")
    private String startTime;
    @ExcelProperty("頒奬日期")
    private String prizeTime;
    @ExcelProperty("比賽是否具代表性")
    private String representative;
    @ExcelProperty("表彰建議")
    private String awardsRemark;
    @ExcelProperty("填表人")
    private String createUserName;
    @ExcelProperty("最高表彰建議(自動生成)")
    private String autoAwardsRemark;
    @ExcelProperty("最終表彰")
    private String finalAwards;
    @ExcelProperty("最終表彰計分")
    private String finalAwardsPoints;
    @ExcelProperty("審核備註")
    private String approveRemark;
}