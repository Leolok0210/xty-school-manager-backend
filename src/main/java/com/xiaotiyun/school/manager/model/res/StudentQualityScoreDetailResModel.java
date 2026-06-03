package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("学生素质评分详情返回信息")
public class StudentQualityScoreDetailResModel {

    @ApiModelProperty("学生姓名")
    private Long studentId;


    @ApiModelProperty("素质项目1评分 * 100")
    private Long qualityProjectScore;

    @ApiModelProperty("素质项目id 0固定为操行")
    private Long qualityProjectId;

    //名称
    @ApiModelProperty("素质项目名称")
    private String qualityProjectName;

    @ApiModelProperty("素质项目等级")
    private String qualityProjectLevel;

    @ApiModelProperty("是否展示分数")
    private boolean display;


}