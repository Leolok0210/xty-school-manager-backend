package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.entity.WechatMiniprogramChannelEntity;
import com.xiaotiyun.school.manager.model.req.ChannelBindReq;
import com.xiaotiyun.school.manager.model.res.MinigrogramAuthResModel;
import com.xiaotiyun.school.manager.model.res.WechatMiniprogramUserChannelResModel;

import java.util.List;

/**
 * 小程序渠道表服务层接口
 */
public interface WechatMiniprogramChannelService extends IService<WechatMiniprogramChannelEntity> {

    Result<?> bindChannel(ChannelBindReq reqModel);

    Result<List<WechatMiniprogramUserChannelResModel>> switchStudent(String code);

    Result<MinigrogramAuthResModel> authChannel(String code, Long channelUserId);

    Result<?> unBindChannel(String code, Long channelUserId);
}
