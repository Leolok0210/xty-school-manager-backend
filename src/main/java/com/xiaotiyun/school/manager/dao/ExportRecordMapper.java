package com.xiaotiyun.school.manager.dao;

import com.xiaotiyun.school.manager.model.entity.ExportRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Akame
* @description 针对表【export_record(导出记录表)】的数据库操作Mapper
* @createDate 2025-09-10 18:05:54
* @Entity com.xiaotiyun.school.manager.model.entity.ExportRecord
*/
@Mapper
public interface ExportRecordMapper extends BaseMapper<ExportRecord> {

}




