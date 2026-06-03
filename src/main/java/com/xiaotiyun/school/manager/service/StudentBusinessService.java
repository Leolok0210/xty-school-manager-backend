package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.StudentBusinessEntity;
import com.xiaotiyun.school.manager.model.req.StudentBusinessPageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentBusinessSaveReqModel;
import com.xiaotiyun.school.manager.model.res.StudentBusinessPageResModel;

public interface StudentBusinessService extends IService<StudentBusinessEntity> {

    PageInfo<StudentBusinessPageResModel> page(Long schoolId, StudentBusinessPageReqModel reqModel);

    void save(Long schoolId, StudentBusinessSaveReqModel reqModel);

    void update(Long id, StudentBusinessSaveReqModel reqModel);

    void delete(Long id);

    String export(Long schoolId, StudentBusinessPageReqModel reqModel);
} 