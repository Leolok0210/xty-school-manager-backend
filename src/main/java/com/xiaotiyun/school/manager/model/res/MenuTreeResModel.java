package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("菜单树响应")
public class MenuTreeResModel {
    @ApiModelProperty("菜单ID")
    private Long id;

    @ApiModelProperty("父级ID")
    private Long parentId;

    @ApiModelProperty("类型(1:菜单,2:按钮)")
    private Integer type;
    
    @ApiModelProperty("菜单名称")
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
    
    @ApiModelProperty("状态(0:隐藏,1:展示)")
    private Integer status;
    
    @ApiModelProperty("子菜单")
    private List<MenuTreeResModel> children;
} 