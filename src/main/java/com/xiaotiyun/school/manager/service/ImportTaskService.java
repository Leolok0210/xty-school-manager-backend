package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.dto.ImportTaskSaveDTO;
import com.xiaotiyun.school.manager.model.entity.ImportTaskEntity;
import com.xiaotiyun.school.manager.model.req.ImportTaskPageReqModel;
import com.xiaotiyun.school.manager.model.res.ImportTaskPageResModel;

public interface ImportTaskService extends IService<ImportTaskEntity> {

    /**
     * 分页查询列表
     */
    PageInfo<ImportTaskPageResModel> page(ImportTaskPageReqModel reqModel);

    /**
     * 新增
     */
    void save(ImportTaskSaveDTO dto);

    /**
     * 修改
     */
    void update(Long id, ImportTaskSaveDTO dto);
}