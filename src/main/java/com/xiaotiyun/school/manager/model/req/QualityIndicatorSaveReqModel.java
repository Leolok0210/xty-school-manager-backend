package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@ApiModel(value = "评价指标保存请求参数")
public class QualityIndicatorSaveReqModel {


    @ApiModelProperty("ID(新增时不传,修改时必传)")
    private Long id;

    @ApiModelProperty(value = "学部(1:幼稚园 2:小学 3:中学)", required = true, example = "1")
    @NotNull(message = "学部不能为空")
    @Min(value = 1, message = "学部值必须在1-3之间")
    @Max(value = 3, message = "学部值必须在1-3之间")
    private Integer department;
    
    @ApiModelProperty(value = "评价指标内容", required = true, example = "课堂纪律")
    @NotBlank(message = "评价指标内容不能为空")
    @Size(max = 50, message = "评价指标内容不能超过50个字")
    private String content;
    
    @ApiModelProperty(value = "权重(1-100的整数)", required = true, example = "20")
    @NotNull(message = "权重不能为空")
    @Min(value = 1, message = "权重必须大于0")
    @Max(value = 100, message = "权重不能超过100")
    private Integer weight;
    
    @ApiModelProperty(value = "展示规则(SCORE-分数,GRADE-评级)", required = true, example = "SCORE")
    @NotBlank(message = "展示规则不能为空")
    private String displayType;
} 