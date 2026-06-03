package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.LeisureCourseOpRecordEntity;
import com.xiaotiyun.school.manager.model.req.LeisureCourseOpRecordQuery;
import com.xiaotiyun.school.manager.model.res.LeisureCourseOpRecordRes;

/**
 * 余暇活动课程操作记录 Service 接口
 */
public interface LeisureCourseOpRecordService extends IService<LeisureCourseOpRecordEntity> {

    PageInfo<LeisureCourseOpRecordRes> page(LeisureCourseOpRecordQuery query);
}
