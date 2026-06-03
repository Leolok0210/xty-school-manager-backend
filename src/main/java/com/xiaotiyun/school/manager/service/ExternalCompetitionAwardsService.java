package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.entity.ExternalCompetitionAwardsEntity;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionAwardsReqModel;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionAwardsSaveReqModel;
import com.xiaotiyun.school.manager.model.res.ExternalCompetitionAwardsResModel;

import java.util.List;

/**
 * 校外活动奖项评级Service接口
 */
public interface ExternalCompetitionAwardsService extends IService<ExternalCompetitionAwardsEntity> {

    /**
     * 分页查询校外活动奖项评级列表
     *
     * @param reqModel 查询条件
     * @param schoolId 学校ID
     * @return 分页结果
     */
    PageInfo<ExternalCompetitionAwardsResModel> pageList(ExternalCompetitionAwardsReqModel reqModel, Long schoolId);

    /**
     * 新增和修改校外活动奖项评级
     *
     * @param reqModels 请求模型列表
     * @param schoolId 学校ID
     * @return 操作结果
     */
    Result<Boolean> addOrUpdate(List<ExternalCompetitionAwardsSaveReqModel> reqModels, Long schoolId);

    /**
     * 删除校外活动奖项评级
     *
     * @param ids 主键ID列表
     * @param schoolId 学校ID
     * @return 操作结果
     */
    Result<Boolean> delete(List<Long> ids, Long schoolId);

    /**
     * 检查奖项评级名称是否存在
     *
     * @param awardsName 奖项评级名称
     * @param schoolId 学校ID
     * @param id 主键ID
     * @return 是否存在
     */
    boolean checkAwardsNameExists(String awardsName, Long schoolId, Long id);
}
