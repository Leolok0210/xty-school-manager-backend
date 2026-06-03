package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.EnterpriseWechatSynRecordEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 企业微信关联错误信息记录表数据访问接口
 */
@Mapper
public interface EnterpriseWechatSynRecordDao extends BaseMapper<EnterpriseWechatSynRecordEntity> {

}