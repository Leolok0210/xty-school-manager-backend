package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.DeviceFaceEntity;

import java.util.List;

public interface DeviceFaceService extends IService<DeviceFaceEntity> {

    DeviceFaceEntity registerFace(String studentId, String name, String deviceSn);

    boolean deleteFace(String studentId);

    List<DeviceFaceEntity> listFaces(String deviceSn);
}
