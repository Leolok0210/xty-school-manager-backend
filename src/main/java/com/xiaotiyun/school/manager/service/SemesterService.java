package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.SemesterEntity;
import com.xiaotiyun.school.manager.model.req.SemesterAddReqModel;
import com.xiaotiyun.school.manager.model.req.SemesterQueryReqModel;
import com.xiaotiyun.school.manager.model.req.SemesterUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.SemesterResModel;

import java.util.List;
import java.util.Map;

public interface SemesterService extends IService<SemesterEntity> {

    /**
     * 删除学段
     */
    void delete(Long id, Long schoolId);

    /**
     * 查询学段列表
     */
    List<SemesterResModel> list(SemesterQueryReqModel reqModel, Long schoolId);

    /**
     * 批量新增学段
     * @param reqModels 学段信息列表
     * @param schoolId 学校ID
     */
    void batchSave(List<SemesterAddReqModel> reqModels, Long schoolId);


    List<SemesterResModel> listByStudent(SemesterQueryReqModel reqModel, Long schoolId);

    Map<Long, String> getNamesByIds(List<Long> ids);
} 