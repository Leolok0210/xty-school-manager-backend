package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("卡片绑定请求")
public class CardBindReqModel {
    @ApiModelProperty("卡片ID")
    private String cardId;
    @ApiModelProperty("学生学号")
    private String studentId;
    @ApiModelProperty("学生姓名")
    private String name;
    @ApiModelProperty("设备序列号")
    private String deviceSn;
}
