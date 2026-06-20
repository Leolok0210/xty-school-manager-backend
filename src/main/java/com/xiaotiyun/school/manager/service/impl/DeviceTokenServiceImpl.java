package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.dao.DeviceTokenDao;
import com.xiaotiyun.school.manager.model.entity.DeviceTokenEntity;
import com.xiaotiyun.school.manager.service.DeviceTokenService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DeviceTokenServiceImpl extends ServiceImpl<DeviceTokenDao, DeviceTokenEntity>
        implements DeviceTokenService {

    @Override
    public String getOrCreateToken(String deviceSn, String deviceName) {
        DeviceTokenEntity existing = baseMapper.findByDeviceSn(deviceSn);
        if (existing != null && existing.getExpireTime().isAfter(LocalDateTime.now())) {
            return existing.getToken();
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        DeviceTokenEntity entity = existing != null ? existing : new DeviceTokenEntity();
        entity.setDeviceSn(deviceSn);
        entity.setDeviceName(deviceName);
        entity.setToken(token);
        entity.setExpireTime(LocalDateTime.now().plusDays(30));
        saveOrUpdate(entity);
        return token;
    }

    @Override
    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) return false;
        DeviceTokenEntity entity = baseMapper.findByToken(token);
        return entity != null && entity.getExpireTime().isAfter(LocalDateTime.now());
    }

    @Override
    public void revokeToken(String deviceSn) {
        DeviceTokenEntity existing = baseMapper.findByDeviceSn(deviceSn);
        if (existing != null) {
            existing.setExpireTime(LocalDateTime.now());
            updateById(existing);
        }
    }
}
