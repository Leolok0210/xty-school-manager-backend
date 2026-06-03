package com.xiaotiyun.school.manager.service;

import com.xiaotiyun.school.manager.model.entity.EnterpriseWechatNoticeEntity;
import com.xiaotiyun.school.manager.model.entity.LeisureActivityRecordEntity;

public interface NoticeService {

    /**
     * 定时任务发送企业微信通知
     */
    void sendEnterpriseWechatNotice();

    /**
     * 创建余暇活动通知发送任务
     */
    void createLeisureNotice(LeisureActivityRecordEntity entity);

    /**
     * 删除余暇活动通知发送任务
     */
    void deleteLeisureNotice(LeisureActivityRecordEntity entity);

    /**
     * 发送通知
     */
    void sendNotice(EnterpriseWechatNoticeEntity entity);

    /**
     * 发送余暇活动通知
     */
    void sendLeisureNotice(LeisureActivityRecordEntity entity, int status);
}