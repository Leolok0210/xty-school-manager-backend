package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.dto.ImportRecordSaveDTO;
import com.xiaotiyun.school.manager.model.entity.ImportRecordEntity;
import com.xiaotiyun.school.manager.model.entity.ImportTaskEntity;
import com.xiaotiyun.school.manager.model.req.ImportRecordPageReqModel;
import com.xiaotiyun.school.manager.model.res.ImportRecordResModel;

import java.util.List;

public interface ImportRecordService extends IService<ImportRecordEntity> {

    /**
     * 分页查询列表
     */
    PageInfo<ImportRecordResModel> page(ImportRecordPageReqModel reqModel);

    /**
     * 新增
     */
    void save(List<ImportRecordSaveDTO> dtoList);

    /**
     * 导出
     */
    String recordExport(Long schoolId, ImportTaskEntity importTask, ImportRecordPageReqModel reqModel);
}