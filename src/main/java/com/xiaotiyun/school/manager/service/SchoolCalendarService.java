package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.SchoolCalendarEntity;
import com.xiaotiyun.school.manager.model.req.SchoolCalendarPageReqModel;
import com.xiaotiyun.school.manager.model.req.SchoolCalendarSaveReqModel;
import com.xiaotiyun.school.manager.model.res.SchoolCalendarPageResModel;
import com.xiaotiyun.school.manager.model.res.SchoolCalendarResModel;
import com.xiaotiyun.school.manager.model.res.SchoolCalendarV230ResModel;

public interface SchoolCalendarService extends IService<SchoolCalendarEntity> {
    PageInfo<SchoolCalendarPageResModel> page(SchoolCalendarPageReqModel reqModel);

    Long save(SchoolCalendarSaveReqModel reqModel);

    void update(Long id, SchoolCalendarSaveReqModel reqModel);

    SchoolCalendarResModel info(Long id);

    SchoolCalendarV230ResModel infoV230(Long id);

    void delete(Long id);
}