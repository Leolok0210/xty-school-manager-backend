package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.ClassroomEntity;
import com.xiaotiyun.school.manager.model.req.ClassroomPageReqModel;
import com.xiaotiyun.school.manager.model.req.ClassroomSaveReqModel;
import com.xiaotiyun.school.manager.model.req.ClassroomTypeReqModel;
import com.xiaotiyun.school.manager.model.res.ClassroomPageResModel;
import com.xiaotiyun.school.manager.model.res.ClassroomTypeResModel;

import java.util.List;

public interface ClassroomService extends IService<ClassroomEntity> {
    Long addType(Long schoolId, ClassroomTypeReqModel reqModel);

    void updateType(Long id, ClassroomTypeReqModel reqModel);

    void deleteType(Long id);

    List<ClassroomTypeResModel> typeList(Long schoolId);

    Long add(Long schoolId, ClassroomSaveReqModel reqModel);

    void update(Long id, ClassroomSaveReqModel reqModel);

    void delete(Long id);

    PageInfo<ClassroomPageResModel> page(Long schoolId, ClassroomPageReqModel reqModel);
} 