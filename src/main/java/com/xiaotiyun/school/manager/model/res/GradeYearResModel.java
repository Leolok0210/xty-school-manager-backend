package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "学年成绩统计返回对象")
public class GradeYearResModel {
    @ApiModelProperty(value = "选择学段时统计数据结果", example = "1")
    private List<GradeYearSemesterResModel> semesters;

    @ApiModelProperty(value = "选择总结时统计数据结果", example = "1")
    private List<GradeYearTotalResModel> totals;
}