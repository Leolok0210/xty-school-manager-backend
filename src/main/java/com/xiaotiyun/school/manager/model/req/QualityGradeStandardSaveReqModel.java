package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@ApiModel("评分标准保存请求")
public class QualityGradeStandardSaveReqModel {
    
    @ApiModelProperty("ID(新增时不传,修改时必传)")
    private Long id;
    
    @NotBlank(message = "评价等级不能为空")
    @Size(max = 10, message = "评价等级不能超过10个字")
    @ApiModelProperty(value = "评价等级", required = true)
    private String grade;

    /**
     * 学部(1:幼稚园 2:小学 3:中学)
     */
    @NotNull(message = "学部不能为空")
    @ApiModelProperty(value = "学部(1:幼稚园 2:小学 3:中学)", required = true)
    private Integer department;
    
    @NotNull(message = "最小分值不能为空")
    @Min(value = 0, message = "最小分值不能小于0")
    @Max(value = 100, message = "最小分值不能大于100")
    @ApiModelProperty(value = "最小分值", required = true)
    private Integer scoreMin;
    
    @NotNull(message = "最大分值不能为空")
    @Min(value = 0, message = "最大分值不能小于0")
    @Max(value = 100, message = "最大分值不能大于100")
    @ApiModelProperty(value = "最大分值", required = true)
    private Integer scoreMax;
} 