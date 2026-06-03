package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.entity.ExternalCompetitionExportRuleEntity;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionExportRuleCheckReqModel;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionExportRuleReqModel;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionExportRuleSaveReqModel;
import com.xiaotiyun.school.manager.model.res.ExternalCompetitionExportRuleResModel;

import java.util.List;

/**
 * 校外活动导出规则表Service接口
 */
public interface ExternalCompetitionExportRuleService extends IService<ExternalCompetitionExportRuleEntity> {

    /**
     * 分页查询校外活动导出规则列表
     *
     * @param reqModel 查询条件
     * @param schoolId 学校ID
     * @return 分页结果
     */
    PageInfo<ExternalCompetitionExportRuleResModel> pageList(ExternalCompetitionExportRuleReqModel reqModel, Long schoolId);

    /**
     * 新增或修改校外活动导出规则
     *
     * @param reqModels 保存模型列表
     * @param schoolId 学校ID
     * @return 操作结果
     */
    Result<Boolean> addOrUpdate(List<ExternalCompetitionExportRuleSaveReqModel> reqModels, Long schoolId);

    /**
     * 删除校外活动导出规则
     *
     * @param ids 规则ID列表
     * @param schoolId 学校ID
     * @return 操作结果
     */
    Result<Boolean> delete(List<Long> ids, Long schoolId);

    /**
     * 检查规则名称是否存在
     *
     * @return 是否存在
     */
    boolean checkRuleExists(ExternalCompetitionExportRuleCheckReqModel reqModel, Long schoolId);
}

