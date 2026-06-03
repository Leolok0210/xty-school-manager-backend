package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.SubstituteRecordEntity;
import com.xiaotiyun.school.manager.model.req.SubstitutePageReqModel;
import com.xiaotiyun.school.manager.model.req.SubstituteSaveReqModel;
import com.xiaotiyun.school.manager.model.req.SubstituteUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.SubstitutePageResModel;

public interface SubstituteRecordService extends IService<SubstituteRecordEntity> {
    void add(Long schoolId, SubstituteSaveReqModel reqModel);

    void update(Long id, SubstituteUpdateReqModel reqModel);

    void delete(Long id);

    PageInfo<SubstitutePageResModel> page(Long schoolId, SubstitutePageReqModel reqModel);

    String export(Long schoolId, SubstitutePageReqModel reqModel);
} 