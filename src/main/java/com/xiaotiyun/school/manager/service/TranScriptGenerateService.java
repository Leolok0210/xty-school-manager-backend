package com.xiaotiyun.school.manager.service;

import com.xiaotiyun.school.manager.model.req.TranScriptGenerateReqModel;
import com.xiaotiyun.school.manager.model.res.TranScriptGenerateResModel;
import com.xiaotiyun.school.manager.model.res.KindergartenTranscriptResModel;

import java.util.List;

public interface TranScriptGenerateService {

    /**
     * 生成成绩单
     * @param reqModel 请求参数
     * @return 成绩单数据
     */
    List<TranScriptGenerateResModel> generate(TranScriptGenerateReqModel reqModel);

    /**
     * 生成幼稚园成绩单
     * @param reqModel 请求参数
     * @return 幼稚园成绩单数据
     */
    List<KindergartenTranscriptResModel> generateKindergarten(TranScriptGenerateReqModel reqModel);





} 