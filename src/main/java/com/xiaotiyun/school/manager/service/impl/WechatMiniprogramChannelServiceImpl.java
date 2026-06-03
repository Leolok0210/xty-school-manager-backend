package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.WechatMiniprogramChannelDao;
import com.xiaotiyun.school.manager.model.dto.WxMiniprogramLoginInfoDTO;
import com.xiaotiyun.school.manager.model.entity.WechatMiniprogramChannelEntity;
import com.xiaotiyun.school.manager.model.entity.WechatMiniprogramUserChannelEntity;
import com.xiaotiyun.school.manager.model.req.ChannelBindReq;
import com.xiaotiyun.school.manager.model.res.MinigrogramAuthResModel;
import com.xiaotiyun.school.manager.model.res.WechatMiniprogramUserChannelResModel;
import com.xiaotiyun.school.manager.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 小程序渠道表服务层实现类
 */
@Service
@RequiredArgsConstructor
public class WechatMiniprogramChannelServiceImpl extends ServiceImpl<WechatMiniprogramChannelDao, WechatMiniprogramChannelEntity> implements WechatMiniprogramChannelService {

    private final EnterpriseWechatService enterpriseWechatService;
    private final UserWeixinRelevanceService userWeixinRelevanceService;
    private final WechatMiniprogramUserChannelService wechatMiniprogramUserChannelService;
    private final WxAuthService wxAuthService;

    private final LanguageUtil languageUtil;

    // uuid, openId
    private final Cache<String, String> wechatMiniprogramUserCache = Caffeine.newBuilder()
            .maximumSize(1000) // 最大缓存条目
            .expireAfterWrite(4, TimeUnit.HOURS)
            .initialCapacity(10)
            .build();

    @Override
    public Result<?> bindChannel(ChannelBindReq reqModel) {
        String openId = null;
        if (StringUtils.isNotEmpty(reqModel.getUserCode())){
            openId = wechatMiniprogramUserCache.getIfPresent(reqModel.getUserCode());
            if (StringUtils.isBlank(openId)) {
                return Result.failed(ResultCode.FAILED.getCode(), LanguageConstants.BIND_TIMEOUT);
            }
        }
        if (StringUtils.isNotEmpty(reqModel.getCode())){
            WxMiniprogramLoginInfoDTO loginInfoDTO = wxAuthService.getOpenIdFromCode(reqModel.getCode());
            if (loginInfoDTO == null) {
                return Result.failed(ResultCode.FAILED.getCode(), LanguageConstants.WX_LOGIN_ERROR);
            }
            openId = loginInfoDTO.getOpenId();
        }
        List<WechatMiniprogramUserChannelEntity> list = wechatMiniprogramUserChannelService.list(new LambdaQueryWrapper<WechatMiniprogramUserChannelEntity>()
                .eq(WechatMiniprogramUserChannelEntity::getStudentId, reqModel.getStudentId())
                .eq(WechatMiniprogramUserChannelEntity::getOpenId, openId));
        if (CollectionUtils.isEmpty(list)) {
            WechatMiniprogramUserChannelEntity entity = new WechatMiniprogramUserChannelEntity();
            entity.setChannelId(reqModel.getChannelId());
            entity.setOpenId(openId);
            entity.setStudentName(reqModel.getStudentName());
            entity.setStudentId(reqModel.getStudentId());
            wechatMiniprogramUserChannelService.save(entity);
            return Result.success(entity.getId());
        } else {
            return Result.success(list.get(0).getId());
        }
    }

    @Override
    public Result<List<WechatMiniprogramUserChannelResModel>> switchStudent(String code) {
        WxMiniprogramLoginInfoDTO loginInfoDTO = wxAuthService.getOpenIdFromCode(code);
        if (loginInfoDTO == null) {
            return Result.failed(ResultCode.FAILED.getCode(), LanguageConstants.WX_LOGIN_ERROR);
        }
        String openId = loginInfoDTO.getOpenId();
        List<WechatMiniprogramUserChannelEntity> list = wechatMiniprogramUserChannelService.list(new LambdaQueryWrapper<WechatMiniprogramUserChannelEntity>()
                .eq(WechatMiniprogramUserChannelEntity::getOpenId, openId));
        if (CollectionUtils.isEmpty(list)) {
            return Result.success(new ArrayList<>());
        }
        Set<Long> channelIdSet = list.stream().map(WechatMiniprogramUserChannelEntity::getChannelId).collect(Collectors.toSet());
        List<WechatMiniprogramChannelEntity> channelList = this.listByIds(new ArrayList<>(channelIdSet));
        Map<Long, WechatMiniprogramChannelEntity> channelMap = channelList.stream()
                .collect(Collectors.toMap(BaseEntity::getId, Function.identity()));
        List<WechatMiniprogramUserChannelResModel> resModelList = list.stream().map(item -> {
            WechatMiniprogramChannelEntity channel = channelMap.get(item.getChannelId());
            WechatMiniprogramUserChannelResModel resModel = new WechatMiniprogramUserChannelResModel();
            resModel.setId(item.getId());
            resModel.setChannelId(item.getChannelId());
            resModel.setChannelPublic(channel.getChannelPublic());
            resModel.setChannelUrl(channel.getChannelUrl());
            resModel.setChannelName(channel.getChannelName());
            resModel.setStudentName(item.getStudentName());
            resModel.setStudentId(item.getStudentId());
            return resModel;
        }).collect(Collectors.toList());
        return Result.success(resModelList);
    }

    @Override
    public Result<MinigrogramAuthResModel> authChannel(String code, Long channelUserId) {
        MinigrogramAuthResModel resModel = new MinigrogramAuthResModel();
        WxMiniprogramLoginInfoDTO loginInfoDTO = wxAuthService.getOpenIdFromCode(code);
        if (loginInfoDTO == null) {
            return Result.failed(ResultCode.FAILED.getCode(), LanguageConstants.WX_LOGIN_ERROR);
        }
        List<WechatMiniprogramUserChannelEntity> list = wechatMiniprogramUserChannelService.list(new LambdaQueryWrapper<WechatMiniprogramUserChannelEntity>()
                .eq(WechatMiniprogramUserChannelEntity::getOpenId, loginInfoDTO.getOpenId()));
        if (CollectionUtils.isEmpty(list)) {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            wechatMiniprogramUserCache.put(uuid, loginInfoDTO.getOpenId());
            resModel.setUserCode(uuid);
            resModel.setChannelBind(false);
            return Result.success(resModel);
        }
        WechatMiniprogramUserChannelEntity userChannelEntity = null;
        resModel.setChannelBind(true);

        Map<Long, WechatMiniprogramUserChannelEntity> channelEntityMap = list.stream()
                .collect(Collectors.toMap(BaseEntity::getId, Function.identity()));
        WechatMiniprogramChannelEntity channel = null;
        if (channelUserId != null && channelEntityMap.containsKey(channelUserId)) {
            userChannelEntity = channelEntityMap.get(channelUserId);
            channel = this.getById(userChannelEntity.getChannelId());
            if (channel == null) {
                return Result.failed(ResultCode.FAILED.getCode(), LanguageConstants.PARAM_ERROR);
            }
        } else {
            if (list.size() == 1){
                userChannelEntity = list.get(0);
                channel = this.getById(userChannelEntity.getChannelId());
            } else {
                List<WechatMiniprogramUserChannelResModel> channelUserList = new ArrayList<>();
                List<Long> channelId = list.stream().map(WechatMiniprogramUserChannelEntity::getChannelId).collect(Collectors.toList());
                List<WechatMiniprogramChannelEntity> channelList = this.listByIds(channelId);
                Map<Long, WechatMiniprogramChannelEntity> channelMap = channelList.stream()
                        .collect(Collectors.toMap(BaseEntity::getId, Function.identity()));
                for (WechatMiniprogramUserChannelEntity wechatMiniprogramUserChannelEntity : list) {
                    WechatMiniprogramUserChannelResModel channelUser = new WechatMiniprogramUserChannelResModel();
                    channelUser.setId(wechatMiniprogramUserChannelEntity.getId());
                    channelUser.setChannelId(wechatMiniprogramUserChannelEntity.getChannelId());
                    channelUser.setStudentName(wechatMiniprogramUserChannelEntity.getStudentName());
                    channelUser.setChannelName(channelMap.get(wechatMiniprogramUserChannelEntity.getChannelId()).getChannelName());
                    channelUser.setChannelPublic(channelMap.get(wechatMiniprogramUserChannelEntity.getChannelId()).getChannelPublic());
                    channelUser.setChannelUrl(channelMap.get(wechatMiniprogramUserChannelEntity.getChannelId()).getChannelUrl());
                    channelUser.setStudentId(wechatMiniprogramUserChannelEntity.getStudentId());
                    channelUserList.add(channelUser);
                }
                resModel.setChannelUser(channelUserList);
                return Result.success(resModel);
            }
        }
        // 设置渠道类型和渠道url
        resModel.setChannelType(channel.getChannelPublic());
        resModel.setUrl(channel.getChannelUrl());
        resModel.setChannelUserId(userChannelEntity.getId());
        // 渠道为公有服务
        if (channel.getChannelPublic() == 1) {
            enterpriseWechatService.authChannelGetUserInfo(loginInfoDTO.getOpenId(), resModel);
            return Result.success(resModel);
        } else {
            WechatMiniprogramUserChannelResModel channelUser = new WechatMiniprogramUserChannelResModel();
            channelUser.setId(userChannelEntity.getId());
            channelUser.setChannelId(userChannelEntity.getChannelId());
            channelUser.setStudentName(userChannelEntity.getStudentName());
            channelUser.setChannelName(channel.getChannelName());
            channelUser.setChannelPublic(channel.getChannelPublic());
            channelUser.setChannelUrl(channel.getChannelUrl());
            channelUser.setStudentId(userChannelEntity.getStudentId());
            resModel.setChannelUser(Collections.singletonList(channelUser));
        }
        // 渠道为其他服务,返回域名
        return Result.success(resModel);
    }

    @Override
    public Result<?> unBindChannel(String code, Long studentId) {
        WxMiniprogramLoginInfoDTO loginInfoDTO = wxAuthService.getOpenIdFromCode(code);
        if (loginInfoDTO == null) {
            return Result.failed(ResultCode.FAILED.getCode(), LanguageConstants.WX_LOGIN_ERROR);
        }
        wechatMiniprogramUserChannelService.remove(new LambdaQueryWrapper<WechatMiniprogramUserChannelEntity>()
                .eq(WechatMiniprogramUserChannelEntity::getStudentId, studentId)
                .eq(WechatMiniprogramUserChannelEntity::getOpenId, loginInfoDTO.getOpenId()));
        return Result.success();
    }
}
