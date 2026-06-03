package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.EnterpriseWechatNoticeEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 企业微信通知DAO层接口
 */
@Mapper
public interface EnterpriseWechatNoticeDao extends BaseMapper<EnterpriseWechatNoticeEntity> {
    
}