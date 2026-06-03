package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class StudentQualityScoreModel {
    @ApiModelProperty(value = "学生ID")
    private Long studentId;
    @ApiModelProperty(value = "中文姓名")
    private String chineseName;
    @ApiModelProperty(value = "座位号")
    private Integer seatNo;
    @ApiModelProperty(value = "学生素质评分详情")
    private List<StudentQualityScoreDetailResModel> resModels;
    @ApiModelProperty(value = "大功")
    private Integer majorMerit;
    @ApiModelProperty(value = "小功")
    private Integer minorMerit;
    @ApiModelProperty(value = "优点")
    private Integer strengths;
    @ApiModelProperty(value = "大过")
    private Integer majorDemerit;
    @ApiModelProperty(value = "小过")
    private Integer minorDemerit;
    @ApiModelProperty(value = "缺点")
    private Integer weaknesses;
    @ApiModelProperty(value = "评语")
    private String comments;
    @ApiModelProperty(value = "评语id 系统默认时，返回null")
    private Long commentsId;
    @ApiModelProperty("英文名")
    private String englishName;
    @ApiModelProperty("迟到次数")
    private Integer lateCount;
    @ApiModelProperty("请假记录")
    private Integer leaveRecords;
    @ApiModelProperty("缺勤次数")
    private Integer absenceCount;
}