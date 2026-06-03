package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ApiModel("学生素质评分添加请求信息")
public class StudentQualityScoreAddReqModel {
    @ApiModelProperty("学年")
    private String sid;

    @NotNull(message = "学段不能为空")
    @ApiModelProperty("学段")
    private Long term;

    @NotNull(message = "学生姓名不能为空")
    @ApiModelProperty("学生姓名")
    private Long studentId;

    @NotNull(message = "班级不能为空")
    @ApiModelProperty("班级")
    private Long classId;

    @NotNull(message = "素质项目1评分 * 100不能为空")
    @ApiModelProperty("素质项目1评分 * 100")
    private Long qualityProjectScore;

    @NotNull(message = "素质项目id不能为空")
    @ApiModelProperty("素质项目id")
    private Long qualityProjectId;
}