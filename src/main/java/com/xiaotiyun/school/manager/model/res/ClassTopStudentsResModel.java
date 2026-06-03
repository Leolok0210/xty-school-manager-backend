package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 各班名列前茅响应参数
 */
@Data
@ApiModel("各班名列前茅响应参数")
public class ClassTopStudentsResModel {

    // 班内号
    @ApiModelProperty("班内号")
    private String classNumber;

    @ApiModelProperty("排名")
    private Integer ranking;

    @ApiModelProperty("学生照片URL")
    private String photoUrl;

    @ApiModelProperty("学生姓名")
    private String studentName;

    @ApiModelProperty("平均分")
    private Double averageScore;

    @ApiModelProperty("操行等级")
    private String conduct;

    @ApiModelProperty("操行分数")
    private Long conductScore;

    @ApiModelProperty("操行展示 1-展示分数")
    private Integer conductDisplay;
}