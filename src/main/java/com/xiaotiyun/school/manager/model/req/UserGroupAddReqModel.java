package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ApiModel("新增用户组请求")
public class UserGroupAddReqModel {
    @NotBlank(message = "用户组名称不能为空")
    @Size(max = 20, message = "用户组名称最长20个字符")
    @ApiModelProperty(value = "用户组名称", required = true)
    private String name;

    @Size(max = 200, message = "备注最长200个字符")
    @ApiModelProperty(value = "备注")
    private String remark;
    
    @NotEmpty(message = "菜单ID列表不能为空")
    @ApiModelProperty(value = "菜单ID列表", required = true)
    private List<Long> menuIds;
} 