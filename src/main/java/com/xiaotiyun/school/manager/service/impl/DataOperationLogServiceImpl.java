package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.dao.DataOperationLogDao;
import com.xiaotiyun.school.manager.model.entity.DataOperationLogEntity;
import com.xiaotiyun.school.manager.service.DataOperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 数据录入记录服务实现类
 */
@Service
public class DataOperationLogServiceImpl extends ServiceImpl<DataOperationLogDao, DataOperationLogEntity> implements DataOperationLogService {
    @Override
    public boolean add(DataOperationLogEntity entity) {
        return this.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchAdd(List<DataOperationLogEntity> entityList) {
        return this.saveBatch(entityList);
    }
}
