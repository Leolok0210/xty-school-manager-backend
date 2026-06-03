package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("科目关联批量修改序号请求")
public class SubjectRelBatchUpdateNumberReqModel {
    @ApiModelProperty("主键ID")
    @NotNull(message = "主键ID不能为空")
    private Long id;

    @ApiModelProperty("序号")
    @NotNull(message = "序号不能为空")
    private Integer number;
} 