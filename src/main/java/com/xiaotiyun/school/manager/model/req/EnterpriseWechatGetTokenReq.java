package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("获取企业微信token请求参数")
public class EnterpriseWechatGetTokenReq {

    private String suite_id;

    private String suite_secret;

    private String suite_ticket;
}
