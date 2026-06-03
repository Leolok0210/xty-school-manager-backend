package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@ApiModel("评分标准批量保存请求")
public class QualityGradeStandardBatchSaveReqModel {
    
    @Valid
    @NotEmpty(message = "评分标准列表不能为空")
    @ApiModelProperty(value = "评分标准列表(新增或修改)", required = true)
    private List<QualityGradeStandardSaveReqModel> standards;
    
    @ApiModelProperty("需要删除的评分标准ID列表")
    private List<Long> deleteIds;
} 