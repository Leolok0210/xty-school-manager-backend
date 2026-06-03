package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("医院信息返回")
public class HospitalResModel {
    @ApiModelProperty("医院ID")
    private Long id;

    @ApiModelProperty("医院名称")
    private String name;

    @ApiModelProperty("医院电话")
    private String phone;

    @ApiModelProperty("医院地址")
    private String address;

    @ApiModelProperty("备注说明")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
} 