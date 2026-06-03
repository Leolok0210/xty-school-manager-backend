package com.xiaotiyun.school.manager.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.xiaotiyun.school.manager.config.WxMaProperties;
import com.xiaotiyun.school.manager.model.dto.WxMiniprogramLoginInfoDTO;
import com.xiaotiyun.school.manager.service.WxAuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WxAuthServiceImpl implements WxAuthService{
    @Autowired
    private WxMaProperties wxMaProperties;
    public WxMiniprogramLoginInfoDTO getOpenIdFromCode(String code) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("appid", wxMaProperties.getAppid());
        parameters.put("secret", wxMaProperties.getSecret());
        parameters.put("js_code", code);
        parameters.put("grant_type", "authorization_code");
        String content = null;
        try {
            content = HttpUtil.get(wxMaProperties.getUrl(), parameters,3000);
        }catch (Exception e)
        {
            log.error("请求获取小程序登入信息失败content:{},e:",content,e);
            return null;
        }
        if(log.isDebugEnabled())
        {
            log.debug("getOpenIdFromCode:{}",content);
        }
        if(!StringUtils.isNotBlank(content))
        {
            log.error("获取openId失败, content is null");
            return null;
        }
        WxMiniprogramLoginInfoDTO infoDTO = new WxMiniprogramLoginInfoDTO();
        JSONObject jsonResponse = JSONObject.parseObject(content);
        Integer errcode = jsonResponse.getInteger("errcode");
        infoDTO.setErrcode(errcode);
        // 获取openId
        String openId = jsonResponse.getString("openid");
        if (openId == null || openId.isEmpty()) {
            log.error("获取openId失败,openid is null");
            return null;
        }
        infoDTO.setOpenId(openId);
        // 获取session_key
        String sessionKey = jsonResponse.getString("session_key");
        infoDTO.setSessionKey(sessionKey);
        // 获取unionid
        String unionid = jsonResponse.getString("unionid");
        infoDTO.setUnionId(unionid);
        //errmsg
        String errmsg = jsonResponse.getString("errmsg");
        infoDTO.setErrmsg(errmsg);
        return infoDTO;
    }
}
