package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.dto.CompetitionStudentCountDTO;
import com.xiaotiyun.school.manager.model.entity.CompetitionRecordEntity;
import com.xiaotiyun.school.manager.model.req.CompetitionRecordBatchCreateReqModel;
import com.xiaotiyun.school.manager.model.req.CompetitionRecordPageReqModel;
import com.xiaotiyun.school.manager.model.req.CompetitionRecordStudentPageReqModel;
import com.xiaotiyun.school.manager.model.req.CompetitionRecordUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.CompetitionRecordResModel;
import com.xiaotiyun.school.manager.model.res.CompetitionStudentPageResModel;

import java.util.List;

public interface CompetitionRecordService extends IService<CompetitionRecordEntity> {
    CompetitionRecordEntity updateRecord(Long recordId, CompetitionRecordUpdateReqModel reqModel);

    List<CompetitionRecordEntity> batchCreateRecords(CompetitionRecordBatchCreateReqModel reqModel);

    PageInfo<CompetitionRecordResModel> getRecordPage(CompetitionRecordPageReqModel reqModel);

    void deleteRecord(Long recordId);

    /**
     * 分页查询学生比赛列表
     *
     * @param reqModel 包含分页参数和查询条件的请求模型
     * @return 带分页信息的比赛列表
     */
    PageInfo<CompetitionStudentPageResModel> studentPage(CompetitionRecordStudentPageReqModel reqModel);

    List<CompetitionStudentCountDTO> getCountStudent(Long classId,Long periodId);
} 