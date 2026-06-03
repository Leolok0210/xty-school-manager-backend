package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("成绩单详情查询请求")
public class TranScriptDetailsQueryReqModel{
    @ApiModelProperty("班级ID")
    private Long classId;
}