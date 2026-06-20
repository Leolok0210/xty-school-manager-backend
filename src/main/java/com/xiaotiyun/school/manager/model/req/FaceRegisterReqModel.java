package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("人脸注册请求")
public class FaceRegisterReqModel {
    @ApiModelProperty("学生学号")
    private String studentId;
    @ApiModelProperty("学生姓名")
    private String name;
    @ApiModelProperty("设备序列号")
    private String deviceSn;
}
