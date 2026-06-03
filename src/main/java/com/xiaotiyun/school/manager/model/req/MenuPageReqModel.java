package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@ApiModel("菜单分页查询参数")
public class MenuPageReqModel {
    
    @ApiModelProperty("菜单名称")
    private String menuName;
    
    @ApiModelProperty("类型(1:菜单,2:按钮)")
    private Integer type;
    
    @ApiModelProperty("状态")
    private String status;
} 