package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class StudentExamScoreCheckResModel {
    @ApiModelProperty("学生id")
    private Long studentId;
    @ApiModelProperty("座位号")
    private Integer seatNo;
    @ApiModelProperty("学生姓名")
    private String studentName;
    @ApiModelProperty("学生成绩列表")
    private List<StudentExamScoreCheckDetailResModel> scoreList;
}