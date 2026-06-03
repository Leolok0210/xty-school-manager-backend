package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("用户组详情响应")
public class UserGroupDetailResModel {
    @ApiModelProperty("用户组ID")
    private Long id;
    
    @ApiModelProperty("用户组名称")
    private String name;
    
    @ApiModelProperty("学校ID")
    private Long schoolId;
    
    @ApiModelProperty("备注")
    private String remark;
    
    @ApiModelProperty("菜单ID列表")
    private List<Long> menuIds;
    
    @ApiModelProperty("关联用户数")
    private Long userCount;
    
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("预设用户组")
    private boolean preset;
} 