package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import com.xiaotiyun.school.manager.model.entity.ActProcessTemplateEntity;
import com.xiaotiyun.school.manager.model.req.ActProcessTemplateSaveReqModel;
import com.xiaotiyun.school.manager.model.res.ActProcessTemplateInfoResModel;
import com.xiaotiyun.school.manager.model.res.ActProcessTemplateListResModel;
import com.xiaotiyun.school.manager.model.res.ActProcessTemplatePageResModel;

import java.util.List;

public interface ActProcessTemplateService extends IService<ActProcessTemplateEntity> {

    PageInfo<ActProcessTemplatePageResModel> page(Long schoolId, PageReqModel reqModel);

    void save(Long schoolId, ActProcessTemplateSaveReqModel reqModel);

    void update(Long schoolId, Long id, ActProcessTemplateSaveReqModel reqModel);

    ActProcessTemplateInfoResModel info(Long id);

    void delete(Long id);

    List<ActProcessTemplateListResModel> list(Long schoolId, Long userId, Integer processType);

    /**
     * 初始化学校审批模板
     *
     * @param schoolId 学校ID
     */
    void initSchoolTemplates(Long schoolId);
}