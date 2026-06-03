package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class WxMiniprogramLoginInfoDTO implements Serializable {


    private static final long serialVersionUID = 1L;
    
    private String openId;

    private String sessionKey;

    private String unionId;

    private Integer errcode;

    private String errmsg;
}
