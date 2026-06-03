package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.ExternalCompetitionEntity;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionCreateReqModel;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionQueryReqModel;
import com.xiaotiyun.school.manager.model.res.ExternalCompetitionPageResModel;

public interface ExternalCompetitionService extends IService<ExternalCompetitionEntity> {

    Long saveOrUpdate(ExternalCompetitionCreateReqModel reqModel);

    ExternalCompetitionPageResModel info(Long id);

    PageInfo<ExternalCompetitionPageResModel> page(ExternalCompetitionQueryReqModel reqModel);

    void delete(Long id);

    String export(ExternalCompetitionQueryReqModel reqModel);
}
