package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.EnterpriseWechatNoticeEntity;
import com.xiaotiyun.school.manager.model.req.EnterpriseWechatNoticePageReqModel;
import com.xiaotiyun.school.manager.model.req.EnterpriseWechatNoticeSaveReqModel;
import com.xiaotiyun.school.manager.model.req.EnterpriseWechatNoticeUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.EnterpriseWechatNoticeResModel;

/**
 * 企业微信通知Service层接口
 */
public interface EnterpriseWechatNoticeService extends IService<EnterpriseWechatNoticeEntity> {

    /**
     * 分页查询企业微信通知列表
     *
     * @param reqModel 分页查询参数
     * @return 分页查询结果
     */
    PageInfo<EnterpriseWechatNoticeResModel> page(Long schoolId, EnterpriseWechatNoticePageReqModel reqModel);

    /**
     * 创建企业微信通知
     *
     * @param schoolId 创建参数
     * @param userId   创建参数
     * @param reqModel 创建参数
     * @return 通知ID
     */
    Long create(Long schoolId, Long userId, EnterpriseWechatNoticeSaveReqModel reqModel);

    /**
     * 修改企业微信通知
     *
     * @param id       通知ID
     * @param reqModel 修改参数
     * @return 操作结果
     */
    void update(Long id, EnterpriseWechatNoticeUpdateReqModel reqModel);

    /**
     * 删除企业微信通知
     *
     * @param id 通知ID
     * @return 操作结果
     */
    Boolean delete(Long id);
}