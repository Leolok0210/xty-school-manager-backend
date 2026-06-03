package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@ApiModel("菜单保存参数")
public class MenuSaveReqModel {
    
    @ApiModelProperty(value = "类型(1:菜单,2:按钮)", required = true)
    @NotNull(message = "类型不能为空")
    private Integer type;
    
    @ApiModelProperty(value = "上级菜单ID", required = true)
    @NotNull(message = "上级菜单不能为空")
    private Long parentId;
    
    @ApiModelProperty(value = "菜单名称", required = true)
    @NotBlank(message = "菜单名称不能为空")
    @Size(min = 1, max = 200, message = "菜单名称长度必须在1-200字符之间")
    private String menuName;
    
    @ApiModelProperty("路由路径")
    private String routePath;
    
    @ApiModelProperty("组件路径")
    private String componentPath;
    
    @ApiModelProperty("权限标识")
    private String permission;
    
    @ApiModelProperty("图标")
    private String icon;
    
    @ApiModelProperty("排序值")
    private Integer sort;
    
    @ApiModelProperty(value = "状态(0:隐藏,1:展示)", required = true)
    @NotNull(message = "状态不能为空")
    private Integer status;
} 