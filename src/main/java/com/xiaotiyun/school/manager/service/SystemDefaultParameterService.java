package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.entity.SystemDefaultParameterEntity;
import com.xiaotiyun.school.manager.model.req.SystemDefaultParameterAddReqModel;
import com.xiaotiyun.school.manager.model.req.SystemDefaultParameterQueryReqModel;
import com.xiaotiyun.school.manager.model.req.SystemDefaultParameterUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.SystemDefaultParameterResModel;

/**
 * 系统字典服务接口
 */
public interface SystemDefaultParameterService extends IService<SystemDefaultParameterEntity> {
    /**
     * 根据请求参数查询系统字典列表
     *
     * @param reqModel 请求参数对象
     * @return 包含系统字典列表的结果对象
     */
    Result<Page<SystemDefaultParameterResModel>> listSystemDefaultParameter(SystemDefaultParameterQueryReqModel reqModel);

    /**
     * 添加新的系统字典记录
     *
     * @param entity 系统字典实体对象
     * @return 操作结果对象
     */
    Result<String> addSystemDefaultParameter(SystemDefaultParameterAddReqModel entity, Long schoolId);

    /**
     * 更新系统字典记录
     *
     * @param entity 系统字典实体对象
     * @return 操作结果对象
     */
    Result<String> updateSystemDefaultParameter(SystemDefaultParameterUpdateReqModel entity);

    /**
     * 删除指定ID的系统字典记录
     *
     * @param id 系统字典记录ID
     * @return 操作结果对象
     */
    Result<String> deleteSystemDefaultParameter(Long id);
}