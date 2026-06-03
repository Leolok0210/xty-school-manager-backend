package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@ApiModel(description = "素质评价指标列表返回模型")
public class QualityIndicatorListResModel {
    
    @ApiModelProperty(value = "指标ID", example = "1")
    private Long id;

    /**
     * 学校ID
     */
    @ApiModelProperty(value = "学校ID", example = "1001")
    private Long schoolId;

    /**
     * 学部(1:幼稚园 2:小学 3:中学)
     */
    @ApiModelProperty(value = "学部", example = "1", notes = "1:幼稚园 2:小学 3:中学")
    private Integer department;

    /**
     * 评价指标内容
     */
    @ApiModelProperty(value = "评价指标内容", example = "学习能力")
    private String content;

    /**
     * 权重(百分比)
     */
    @ApiModelProperty(value = "权重", example = "30", notes = "百分比表示，范围1-100")
    private Integer weight;

    /**
     * 展示规则(SCORE-分数,GRADE-评级)
     */
    @ApiModelProperty(value = "展示规则", example = "SCORE", notes = "SCORE-分数,GRADE-评级")
    private String displayType;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}