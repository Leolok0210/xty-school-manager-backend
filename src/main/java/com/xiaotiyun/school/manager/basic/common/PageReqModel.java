package com.xiaotiyun.school.manager.basic.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("分页查询参数")
public class PageReqModel implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @NotNull(message = "页码不能为空")
    @ApiModelProperty(value = "页码", example = "1")
    private Integer pageNum = 1;
    @NotNull(message = "每页条数不能为空")
    @ApiModelProperty(value = "每页条数", example = "10")
    private Integer pageSize = 10;
} 