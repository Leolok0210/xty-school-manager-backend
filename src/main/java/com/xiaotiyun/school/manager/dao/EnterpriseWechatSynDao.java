package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.EnterpriseWechatSynEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 企业微信关联同步表数据访问接口
 */
@Mapper
public interface EnterpriseWechatSynDao extends BaseMapper<EnterpriseWechatSynEntity> {

}