package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.DataOperationLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据录入记录DAO接口
 */
@Mapper
public interface DataOperationLogDao extends BaseMapper<DataOperationLogEntity> {
}
