package com.xiaotiyun.school.manager.service;

import com.xiaotiyun.school.manager.model.entity.DataOperationLogEntity;

import java.util.List;

/**
 * 数据录入记录服务接口
 */
public interface DataOperationLogService {
    /**
     * 新增数据录入记录
     *
     * @param entity 实体对象
     * @return 是否成功
     */
    boolean add(DataOperationLogEntity entity);

    /**
     * 批量新增数据录入记录
     *
     * @param entityList 实体对象列表
     * @return 是否成功
     */
    boolean batchAdd(List<DataOperationLogEntity> entityList);
}
