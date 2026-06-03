package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.TranscriptDetailsEntity;
import com.xiaotiyun.school.manager.model.req.TranScriptDetailsQueryReqModel;
import com.xiaotiyun.school.manager.model.req.TranScriptDetailsSaveReqModel;
import com.xiaotiyun.school.manager.model.res.TranScriptDetailsResModel;

import java.util.List;

public interface TranScriptDetailsService extends IService<TranscriptDetailsEntity> {
    
    /**
     * 查询成绩单详情列表
     */
    List<TranScriptDetailsResModel> list(TranScriptDetailsQueryReqModel reqModel, Long schoolId);

    /**
     * 保存或更新成绩单详情
     */
    Boolean saveOrUpdate(TranScriptDetailsSaveReqModel reqModel, Long schoolId);

    /**
     * 删除成绩单详情
     */
    Boolean delete(Long id, Long schoolId);

    /**
     * 获取成绩单详情
     */
    TranScriptDetailsResModel info(Long id, Long schoolId);
} 