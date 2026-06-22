package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.DeviceAttendanceEntity;
import com.xiaotiyun.school.manager.model.req.DeviceAttendanceReqModel;
import com.xiaotiyun.school.manager.model.res.DeviceAttendanceResModel;

import java.util.List;
import java.util.Map;

public interface DeviceAttendanceService extends IService<DeviceAttendanceEntity> {
    DeviceAttendanceResModel record(DeviceAttendanceReqModel reqModel);

    List<String> batchRecord(List<DeviceAttendanceReqModel> records);

    List<DeviceAttendanceResModel> queryRecords(String date, Long classId);

    Object stats(String type, String date, Long classId);

    Map<String, Object> queryRecordsPage(int pageNum, int pageSize, String date, Long classId, String personType);

    Map<String, Object> statsForManage(String type, String date, Long classId, String personType);
}
