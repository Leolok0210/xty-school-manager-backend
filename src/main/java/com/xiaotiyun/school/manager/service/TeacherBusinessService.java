package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.TeacherBusinessEntity;
import com.xiaotiyun.school.manager.model.req.TeacherBusinessPageReqModel;
import com.xiaotiyun.school.manager.model.req.TeacherBusinessSaveReqModel;
import com.xiaotiyun.school.manager.model.req.TeacherBusinessStartReqModel;
import com.xiaotiyun.school.manager.model.res.TeacherBusinessPageResModel;

public interface TeacherBusinessService extends IService<TeacherBusinessEntity> {

    PageInfo<TeacherBusinessPageResModel> page(TeacherBusinessPageReqModel reqModel);

    void save(TeacherBusinessSaveReqModel reqModel);

    void update(Long id, TeacherBusinessSaveReqModel reqModel);

    void delete(Long id);

    String export(TeacherBusinessPageReqModel reqModel);

    void start(Long schoolId, Long userId, TeacherBusinessStartReqModel reqModel);
} 