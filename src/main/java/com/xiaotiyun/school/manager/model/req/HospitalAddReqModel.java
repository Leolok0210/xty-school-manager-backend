package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@ApiModel("新增医院请求")
public class HospitalAddReqModel {
    @NotBlank(message = "医院名称不能为空")
    @Size(max = 20, message = "医院名称最长20字")
    @ApiModelProperty(value = "医院名称", required = true)
    private String name;

    @NotBlank(message = "医院电话不能为空")
    @ApiModelProperty(value = "医院电话", required = true)
    private String phone;

    @NotBlank(message = "医院地址不能为空")
    @Size(max = 100, message = "医院地址最长100字")
    @ApiModelProperty(value = "医院地址", required = true)
    private String address;

    @Size(max = 500, message = "备注说明最长500字")
    @ApiModelProperty(value = "备注说明", required = true)
    private String remark;
} 