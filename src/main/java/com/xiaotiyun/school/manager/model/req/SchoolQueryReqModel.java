package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@ApiModel("学校查询请求")
public class SchoolQueryReqModel extends PageReqModel {
    @ApiModelProperty("学校名称")
    private String name;
    
    @ApiModelProperty("学校类型(1:幼稚园 2:小学 3:中学)")
    private String schoolType;
    
    @ApiModelProperty("省份")
    private String province;
    
    @ApiModelProperty("城市")
    private String city;
    
    @ApiModelProperty("区县")
    private String district;
    
    @ApiModelProperty("有效期开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expireTimeStart;
    
    @ApiModelProperty("有效期结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expireTimeEnd;

    @ApiModelProperty("状态(null全部，true未过期，false已过期)")
    private Boolean status;
}