package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentGraduateExamScorePageResModel {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("学生id")
    private Long studentId;
    @ApiModelProperty("座位号")
    private Integer seatNo;
    @ApiModelProperty("学生姓名")
    private String studentName;
    @ApiModelProperty("学生编号")
    private String studentNo;
    @ApiModelProperty("成绩*100")
    private Integer score;
    @ApiModelProperty("录入时间")
    private LocalDateTime createTime;
    @ApiModelProperty("更新人")
    private String updateUser;
}