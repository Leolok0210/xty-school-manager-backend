package com.xiaotiyun.school.manager.service;


import com.xiaotiyun.school.manager.model.dto.WxMiniprogramLoginInfoDTO;

public interface WxAuthService {

    WxMiniprogramLoginInfoDTO getOpenIdFromCode(String code);
}
