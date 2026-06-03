package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("获取企业微信用户请求参数")
public class EnterpriseWechatGetUserReq {

    private String suite_access_token;

    private String code;

}
