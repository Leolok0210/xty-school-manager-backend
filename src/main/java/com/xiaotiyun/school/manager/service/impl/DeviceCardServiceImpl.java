package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.dao.DeviceCardDao;
import com.xiaotiyun.school.manager.model.entity.DeviceCardEntity;
import com.xiaotiyun.school.manager.service.DeviceCardService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceCardServiceImpl extends ServiceImpl<DeviceCardDao, DeviceCardEntity>
        implements DeviceCardService {

    @Override
    public DeviceCardEntity bindCard(String cardId, String studentId, String name, String deviceSn) {
        DeviceCardEntity existing = baseMapper.findByCardId(cardId);
        if (existing != null) {
            existing.setStudentId(studentId);
            existing.setName(name);
            existing.setDeviceSn(deviceSn);
            updateById(existing);
            return existing;
        }
        DeviceCardEntity entity = new DeviceCardEntity();
        entity.setCardId(cardId);
        entity.setStudentId(studentId);
        entity.setName(name);
        entity.setDeviceSn(deviceSn);
        save(entity);
        return entity;
    }

    @Override
    public boolean unbindCard(String cardId) {
        DeviceCardEntity existing = baseMapper.findByCardId(cardId);
        if (existing == null) {
            return false;
        }
        return removeById(existing.getId());
    }

    @Override
    public List<DeviceCardEntity> listBindings(String deviceSn) {
        if (deviceSn != null && !deviceSn.isEmpty()) {
            return baseMapper.findByDeviceSn(deviceSn);
        }
        return list();
    }
}
