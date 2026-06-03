package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@ApiModel("批量开通学校菜单请求")
public class SchoolMenuBatchUpdateReqModel {
    @NotEmpty(message = "学校ID列表不能为空")
    @ApiModelProperty(value = "学校ID列表", required = true)
    private List<Long> schoolIds;
    
    @NotEmpty(message = "菜单ID列表不能为空")
    @ApiModelProperty(value = "菜单ID列表", required = true)
    private List<Long> menuIds;
} 