package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "学年成绩学段统计返回对象")
public class GradeYearSemesterResModel {
    @ApiModelProperty(value = "科目ID", example = "1")
    private Long subjectId;

    @ApiModelProperty(value = "科目名称", example = "高一")
    private String subjectName;

    @ApiModelProperty(value = "合格人数", example = "1")
    private Integer qualifiedCount;

    @ApiModelProperty(value = "合格率", example = "1")
    private String qualifiedRate;

    @ApiModelProperty(value = "不合格人数", example = "1")
    private Integer flunkCount;

    @ApiModelProperty(value = "不合格率", example = "1")
    private String flunkRate;

    @ApiModelProperty(value = "60-80人数", example = "1")
    private Integer sixtyToEightyCount;

    @ApiModelProperty(value = "60-80率", example = "1")
    private String sixtyToEightyRate;

    @ApiModelProperty(value = "80-90人数", example = "1")
    private Integer eightyToNinetyCount;

    @ApiModelProperty(value = "80-90率", example = "1")
    private String eightyToNinetyRate;

    @ApiModelProperty(value = "90-100人数", example = "1")
    private Integer ninetyToHundredCount;

    @ApiModelProperty(value = "90-100率", example = "1")
    private String ninetyToHundredRate;
}