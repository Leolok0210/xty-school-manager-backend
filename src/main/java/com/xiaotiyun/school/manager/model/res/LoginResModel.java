package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("登录响应")
public class LoginResModel {
    @ApiModelProperty("token")
    private String token;
    
    @ApiModelProperty("是否需要重置密码(0:不需要 1:需要)")
    private Integer needResetPwd;

    private UserDetailResModel userDetailResModel;

    @ApiModelProperty("小程序用户信息")
    private MinigrogramUserDetailResModel user;

    /**
     * 学校信息列表
     */
    private List<SchoolInfoResModel> schoolInfoList;
}