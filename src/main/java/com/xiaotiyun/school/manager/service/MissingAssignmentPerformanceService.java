package com.xiaotiyun.school.manager.service;

import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.MissingAssignmentPerformance;
import com.xiaotiyun.school.manager.model.req.MissingAssignmentPerformanceQueryReqModel;
import com.xiaotiyun.school.manager.model.res.MissingAssignmentPerformanceDetailResModel;

import java.util.List;

public interface MissingAssignmentPerformanceService {
    List<MissingAssignmentPerformance> createMissingAssignmentPerformances(List<MissingAssignmentPerformance> missingAssignmentPerformances);
    MissingAssignmentPerformance updateMissingAssignmentPerformance(MissingAssignmentPerformance missingAssignmentPerformance);
    void deleteMissingAssignmentPerformance(Long id);
    MissingAssignmentPerformance getMissingAssignmentPerformanceById(Long id);
    PageInfo<MissingAssignmentPerformanceDetailResModel> getMissingAssignmentPerformanceList(MissingAssignmentPerformanceQueryReqModel reqModel);

    /**
     * 查询学段内是否有欠交作业记录
     */
    boolean hasPerformance(Long periodId);
}