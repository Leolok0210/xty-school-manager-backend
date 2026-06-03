package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "学年成绩总结统计返回对象")
public class GradeYearTotalDetailResModel {
    @ApiModelProperty(value = "学段ID", example = "1")
    private Long semesterId;

    @ApiModelProperty(value = "学段名称", example = "高一")
    private String semesterName;

    @ApiModelProperty(value = "平均分", example = "1")
    private String avgScore;
}