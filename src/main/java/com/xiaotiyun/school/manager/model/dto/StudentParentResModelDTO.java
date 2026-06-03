package com.xiaotiyun.school.manager.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StudentParentResModelDTO {
    @ApiModelProperty(value = "家长的userid")
    private String parent_userid;

    @ApiModelProperty(value = "学生与家长的关系")
    private String relation;

    @ApiModelProperty(value = "家长手机号，第三方不可获取")
    private String mobile;

    @ApiModelProperty(value = "家长是否关注了\"学校通知\"，0-未关注，1-已关注")
    private Integer is_subscribe;

    @ApiModelProperty(value = "家长的external_userid,仅当家长已关注才返回")
    private String external_userid;
}
