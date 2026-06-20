package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.dao.DeviceFaceDao;
import com.xiaotiyun.school.manager.model.entity.DeviceFaceEntity;
import com.xiaotiyun.school.manager.service.DeviceFaceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceFaceServiceImpl extends ServiceImpl<DeviceFaceDao, DeviceFaceEntity>
        implements DeviceFaceService {

    @Override
    public DeviceFaceEntity registerFace(String studentId, String name, String deviceSn) {
        DeviceFaceEntity existing = baseMapper.findByStudentId(studentId);
        if (existing != null) {
            existing.setName(name);
            existing.setDeviceSn(deviceSn);
            existing.setStatus("registered");
            updateById(existing);
            return existing;
        }
        DeviceFaceEntity entity = new DeviceFaceEntity();
        entity.setStudentId(studentId);
        entity.setName(name);
        entity.setDeviceSn(deviceSn);
        entity.setStatus("registered");
        save(entity);
        return entity;
    }

    @Override
    public boolean deleteFace(String studentId) {
        DeviceFaceEntity existing = baseMapper.findByStudentId(studentId);
        if (existing == null) {
            return false;
        }
        return removeById(existing.getId());
    }

    @Override
    public List<DeviceFaceEntity> listFaces(String deviceSn) {
        if (deviceSn != null && !deviceSn.isEmpty()) {
            return baseMapper.findByDeviceSn(deviceSn);
        }
        return list();
    }
}
