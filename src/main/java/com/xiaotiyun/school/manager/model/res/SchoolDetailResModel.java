package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("学校详情响应")
public class SchoolDetailResModel {
    @ApiModelProperty("学校ID")
    private Long id;
    
    @ApiModelProperty("学校名称")
    private String name;
    
    @ApiModelProperty("学校编号")
    private String code;
    
    @ApiModelProperty("省份")
    private String province;
    
    @ApiModelProperty("城市")
    private String city;
    
    @ApiModelProperty("区县")
    private String district;
    
    @ApiModelProperty("详细地址")
    private String address;
    
    @ApiModelProperty("学校类型列表")
    private List<Integer> schoolTypes;
    
    @ApiModelProperty("有效期截止时间")
    private LocalDateTime expireTime;
    
    @ApiModelProperty("备注")
    private String remark;
    
    @ApiModelProperty("开通菜单ID列表")
    private List<Long> menuIds;
    
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    
    @ApiModelProperty("状态(true:有效 false:已过期)")
    private Boolean status;

    @ApiModelProperty("企微绑定名称")
    private String entWechatName;

    @ApiModelProperty("是否发起过健康申报，0-未申报，1-已申报")
    private Integer isHealthDeclared;
} 