package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.entity.ExternalCompetitionCategoryEntity;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionCategoryReqModel;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionCategorySaveReqModel;
import com.xiaotiyun.school.manager.model.res.ExternalCompetitionCategoryResModel;

import java.util.List;

/**
 * 校外活动范畴表Service接口
 */
public interface ExternalCompetitionCategoryService extends IService<ExternalCompetitionCategoryEntity> {

    /**
     * 分页查询校外活动范畴列表
     *
     * @param reqModel 查询条件
     * @param schoolId 学校ID
     * @return 分页结果
     */
    PageInfo<ExternalCompetitionCategoryResModel> pageList(ExternalCompetitionCategoryReqModel reqModel, Long schoolId);

    /**
     * 新增和修改校外活动范畴
     *
     * @param reqModels 请求模型列表
     * @param schoolId 学校ID
     * @return 操作结果
     */
    Result<Boolean> addOrUpdate(List<ExternalCompetitionCategorySaveReqModel> reqModels, Long schoolId);

    /**
     * 删除校外活动范畴
     *
     * @param ids 主键ID列表
     * @param schoolId 学校ID
     * @return 操作结果
     */
    Result<Boolean> delete(List<Long> ids, Long schoolId);

    /**
     * 检查范畴名称是否存在
     *
     * @param categoryName 范畴名称
     * @param schoolId 学校ID
     * @param id 主键ID（可选，用于排除当前记录）
     * @return 是否存在
     */
    boolean checkCategoryNameExists(String categoryName, Long schoolId, Long id);

    /**
     * 获取所有校外活动范畴列表
     *
     * @param schoolId 学校ID
     * @return 范畴列表
     */
    List<ExternalCompetitionCategoryResModel> getAllList(Long schoolId);
}
