package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户组查询请求")
public class UserGroupQueryReqModel extends PageReqModel {
    @ApiModelProperty("用户组名称")
    private String name;
}