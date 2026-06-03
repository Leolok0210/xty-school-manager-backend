package com.xiaotiyun.school.manager.service;

import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.ExportRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.res.ExportRecordResModel;

import java.util.List;

/**
* @author Akame
* @description 针对表【export_record(导出记录表)】的数据库操作Service
* @createDate 2025-09-10 18:05:54
*/
public interface ExportRecordService extends IService<ExportRecord> {


    PageInfo<ExportRecordResModel> list(int pageNum, int pageSize, List<Long> relId, Long schoolId, Integer type);
}
