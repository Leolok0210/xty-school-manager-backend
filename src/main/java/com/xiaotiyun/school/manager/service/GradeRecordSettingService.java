package com.xiaotiyun.school.manager.service;

import com.xiaotiyun.school.manager.model.entity.GradeRecordTimeSettingEntity;
import com.xiaotiyun.school.manager.model.entity.GradeRecordClassSettingEntity;
import com.xiaotiyun.school.manager.model.req.GradeRecordSettingSaveReqModel;
import com.xiaotiyun.school.manager.model.res.GradeRecordSettingResModel;
import java.util.List;

public interface GradeRecordSettingService {
    
    void saveSetting(Long schoolId, GradeRecordSettingSaveReqModel reqModel);
    
    GradeRecordSettingResModel getSetting(Long schoolId, String schoolYear);
}