package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.ClassroomTypeEntity;
import com.xiaotiyun.school.manager.model.req.ClassroomTypeReqModel;
import com.xiaotiyun.school.manager.model.res.ClassroomTypeResModel;

import java.util.List;

public interface ClassroomTypeService extends IService<ClassroomTypeEntity> {
    Long add(Long schoolId, ClassroomTypeReqModel reqModel);

    void update(Long id, ClassroomTypeReqModel reqModel);

    void delete(Long id);

    List<ClassroomTypeResModel> list(Long schoolId);
}