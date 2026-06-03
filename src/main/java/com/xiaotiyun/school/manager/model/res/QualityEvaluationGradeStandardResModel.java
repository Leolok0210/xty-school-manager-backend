package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@ApiModel("评分标准响应模型")
public class QualityEvaluationGradeStandardResModel {
    
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "学校ID")
    private Long schoolId;

    @ApiModelProperty(value = "评价等级", example = "优秀/良好/合格/不合格")
    private String grade;

    @ApiModelProperty(value = "分数区间最小值", example = "80")
    private Integer scoreMin;

    @ApiModelProperty(value = "分数区间最大值", example = "100")
    private Integer scoreMax;

    @ApiModelProperty(value = "学部(1:幼稚园 2:小学 3:中学)")
    private Integer department;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

} 