package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.model.entity.CompetitionEntity;
import com.xiaotiyun.school.manager.model.req.CompetitionPageReqModel;
import com.xiaotiyun.school.manager.model.req.CompetitionSaveReqModel;
import com.xiaotiyun.school.manager.model.res.CompetitionPageResModel;
import com.xiaotiyun.school.manager.model.res.CompetitionRecordResModel;
import com.xiaotiyun.school.manager.model.res.CompetitionResModel;

import java.util.List;

public interface CompetitionService extends IService<CompetitionEntity> {
    /**
     * 分页查询比赛列表
     *
     * @param reqModel 包含分页参数和查询条件的请求模型
     * @return 带分页信息的比赛列表
     */
    PageInfo<CompetitionPageResModel> page(CompetitionPageReqModel reqModel);

    Long save(CompetitionSaveReqModel reqModel);

    void updateCompetition(Long id, CompetitionSaveReqModel reqModel);

    /**
     * 逻辑删除比赛记录
     *
     * @param id 比赛ID
     * @throws BusinessException 当比赛不存在或已被删除时抛出
     */
    void deleteCompetition(Long id);

    /**
     * 根据ID获取比赛详情
     *
     * @param id 比赛ID
     * @return 比赛详细信息
     * @throws BusinessException 当比赛不存在时抛出
     */
    CompetitionResModel getCompetitionById(Long id);
} 