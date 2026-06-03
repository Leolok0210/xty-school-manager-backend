package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.TranscriptRecordEntity;
import com.xiaotiyun.school.manager.model.req.TranscriptRecordQueryReqModel;
import com.xiaotiyun.school.manager.model.req.TranscriptRecordReqModel;
import com.xiaotiyun.school.manager.model.req.TranscriptRecordUpdateReq;
import com.xiaotiyun.school.manager.model.res.TranscriptRecordResModel;

public interface TranscriptRecordService extends IService<TranscriptRecordEntity> {

    void create(TranscriptRecordReqModel reqModel);

    void update(Long schoolId, TranscriptRecordReqModel reqModel);

    void updateStatusAndZipUrl(TranscriptRecordUpdateReq reqModel);

    PageInfo<TranscriptRecordResModel> page(TranscriptRecordQueryReqModel id);

    void delete(Long id);

}