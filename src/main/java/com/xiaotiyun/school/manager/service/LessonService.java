package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.LessonEntity;
import com.xiaotiyun.school.manager.model.req.LessonCopyReqModel;
import com.xiaotiyun.school.manager.model.req.LessonListReqModel;
import com.xiaotiyun.school.manager.model.req.LessonSaveReqModel;
import com.xiaotiyun.school.manager.model.res.LessonResModel;

import java.util.List;

public interface LessonService extends IService<LessonEntity> {
    List<Long> gradeList(Long schoolId);

    Long add(Long schoolId, LessonSaveReqModel reqModel);

    void update(Long id, LessonSaveReqModel reqModel);

    void delete(Long id);

    List<LessonResModel> list(Long schoolId, LessonListReqModel reqModel);

    void copy(Long schoolId, LessonCopyReqModel reqModel);
} 