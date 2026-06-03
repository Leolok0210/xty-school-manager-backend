package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.dto.LeisureActivitiesNoticeSendDTO;
import com.xiaotiyun.school.manager.model.entity.LeisureActivitiesNoticeEntity;
import com.xiaotiyun.school.manager.model.req.LeisureActivitiesNoticeReqModel;
import com.xiaotiyun.school.manager.model.res.LeisureActivitiesNoticeResModel;
import com.xiaotiyun.school.manager.model.res.StudentLeisureActivitiesResultResModel;

/**
 * 余暇活动匹配结果通知Service层接口
 */
public interface LeisureActivitiesNoticeService extends IService<LeisureActivitiesNoticeEntity> {

    /**
     * 余暇活动匹配通知
     *
     * @return
     */
    StudentLeisureActivitiesResultResModel notice(Long studentId, Long periodId);

    /**
     * 发布通知
     *
     * @return
     */
    void sendNotice(LeisureActivitiesNoticeSendDTO sendDTO);
}