package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("新增学校请求")
public class SchoolAddReqModel {
    @NotBlank(message = "学校名称不能为空")
    @Size(max = 50, message = "学校名称最长50个字符")
    @ApiModelProperty(value = "学校名称", required = true)
    private String name;
    
    @Size(max = 20, message = "学校编号最长20个字符")
    @ApiModelProperty("学校编号(为空时自动生成)")
    private String code;
    
    @NotBlank(message = "省份不能为空")
    @ApiModelProperty(value = "省份", required = true)
    private String province;
    
    @NotBlank(message = "城市不能为空")
    @ApiModelProperty(value = "城市", required = true)
    private String city;
    
    @NotBlank(message = "区县不能为空")
    @ApiModelProperty(value = "区县", required = true)
    private String district;
    
    @NotBlank(message = "详细地址不能为空")
    @ApiModelProperty(value = "详细地址", required = true)
    private String address;
    
    @NotEmpty(message = "学校类型不能为空")
    @ApiModelProperty(value = "学校类型列表(1:幼稚园 2:小学 3:中学)", required = true)
    private List<Integer> schoolTypes;
    
    @ApiModelProperty("有效期截止时间(为空表示永久有效)")
    private LocalDateTime expireTime;
    
    @Size(max = 200, message = "备注最长200个字符")
    @ApiModelProperty("备注")
    private String remark;
    
    @NotEmpty(message = "开通菜单不能为空")
    @ApiModelProperty(value = "开通菜单ID列表", required = true)
    private List<Long> menuIds;
} 