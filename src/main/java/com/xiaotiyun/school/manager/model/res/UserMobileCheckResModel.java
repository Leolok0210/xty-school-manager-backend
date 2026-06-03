package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("手机号检查结果")
public class UserMobileCheckResModel {

    @ApiModelProperty("是否存在")
    private Boolean exists;

    @ApiModelProperty("是否已绑定当前学校")
    private Boolean boundCurrentSchool;
} 