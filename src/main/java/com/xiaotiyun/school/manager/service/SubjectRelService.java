package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.SubjectRelEntity;
import com.xiaotiyun.school.manager.model.req.SubjectRelGroupQueryReqModel;
import com.xiaotiyun.school.manager.model.res.SubjectRelResModel;

import java.util.List;

public interface SubjectRelService extends IService<SubjectRelEntity> {

    List<SubjectRelResModel> listByGroup(SubjectRelGroupQueryReqModel reqModel);

    List<SubjectRelResModel> listByIds(List<Long> ids);
} 