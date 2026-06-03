package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "学年成绩总结统计返回对象")
public class GradeYearTotalResModel {
    @ApiModelProperty(value = "科目ID", example = "1")
    private Long subjectId;

    @ApiModelProperty(value = "科目名称", example = "高一")
    private String subjectName;

    @ApiModelProperty(value = "学段平均分数据")
    private List<GradeYearTotalDetailResModel> details;
}