package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.DeviceCardEntity;

import java.util.List;

public interface DeviceCardService extends IService<DeviceCardEntity> {

    DeviceCardEntity bindCard(String cardId, String studentId, String name, String deviceSn);

    boolean unbindCard(String cardId);

    List<DeviceCardEntity> listBindings(String deviceSn);
}
