package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@ApiModel("评价指标批量保存请求")
public class QualityIndicatorBatchSaveReqModel {
    
    @Valid
    @NotEmpty(message = "指标列表不能为空")
    @ApiModelProperty(value = "指标列表(新增或修改)", required = true)
    private List<QualityIndicatorSaveReqModel> indicators;
    
    @ApiModelProperty("需要删除的指标ID列表")
    private List<Long> deleteIds;
} 