package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.QualityEvaluationIndicatorEntity;
import com.xiaotiyun.school.manager.model.res.QualityEvaluationGradeStandardResModel;
import com.xiaotiyun.school.manager.model.req.QualityIndicatorSaveReqModel;
import com.xiaotiyun.school.manager.model.req.QualityGradeStandardSaveReqModel;
import com.xiaotiyun.school.manager.model.res.QualityIndicatorListResModel;
import com.xiaotiyun.school.manager.model.req.QualityIndicatorBatchSaveReqModel;
import com.xiaotiyun.school.manager.model.req.QualityGradeStandardBatchSaveReqModel;

import java.util.List;

public interface QualityEvaluationService extends IService<QualityEvaluationIndicatorEntity> {
    

    List<QualityIndicatorListResModel> listIndicators(Long schoolId);

    List<QualityEvaluationGradeStandardResModel> listGradeStandards(Long schoolId);

    /**
     * 批量保存评价指标
     */
    void batchSaveIndicators(Long schoolId, QualityIndicatorBatchSaveReqModel reqModel);
    
    /**
     * 批量保存评分标准
     */
    void batchSaveGradeStandards(Long schoolId, QualityGradeStandardBatchSaveReqModel reqModel);


    List<QualityIndicatorListResModel> listIndicator(Long schoolId,Integer department);
} 