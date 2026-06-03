package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.EnterpriseWechatSynEntity;
import com.xiaotiyun.school.manager.model.res.EnterpriseWechatSynResModel;

/**
 * 企业微信关联同步表服务接口
 */
public interface EnterpriseWechatSynService extends IService<EnterpriseWechatSynEntity> {

    /**
     * 分页查询企业微信关联同步任务
     */
    PageInfo<EnterpriseWechatSynResModel> list(Integer pageNum, Integer pageSize, Integer type);
}