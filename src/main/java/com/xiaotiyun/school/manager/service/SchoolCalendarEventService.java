package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.SchoolCalendarEventEntity;
import com.xiaotiyun.school.manager.model.req.SchoolCalendarEventSaveReqModel;
import com.xiaotiyun.school.manager.model.req.SchoolCalendarEventSaveV230ReqModel;
import com.xiaotiyun.school.manager.model.req.SchoolCalendarEventUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.SchoolCalendarEventResModel;

import java.util.List;

public interface SchoolCalendarEventService extends IService<SchoolCalendarEventEntity> {
    void save(SchoolCalendarEventSaveReqModel reqModel);

    void addOrEditV230(SchoolCalendarEventSaveV230ReqModel reqModel);

    void update(Long id, SchoolCalendarEventUpdateReqModel reqModel);

    void delete(Long id);

    List<SchoolCalendarEventResModel> listByCalendarId(Long calendarId);
} 