package com.xiaotiyun.school.manager.service;

import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.ClassPerformance;
import com.xiaotiyun.school.manager.model.req.ClassPerformanceQueryReqModel;
import com.xiaotiyun.school.manager.model.req.StudentQualityScoreQueryReqModel;
import com.xiaotiyun.school.manager.model.res.ClassPerformanceDetailResModel;
import com.xiaotiyun.school.manager.model.res.StudentQualityScoreModel;

import java.util.List;

public interface ClassPerformanceService {
    List<ClassPerformance> createClassPerformances(List<ClassPerformance> classPerformances);
    ClassPerformance updateClassPerformance(ClassPerformance classPerformance);
    void deleteClassPerformance(Long id);
    ClassPerformance getClassPerformanceById(Long id);
    PageInfo<ClassPerformanceDetailResModel> getClassPerformanceList(ClassPerformanceQueryReqModel reqModel);

    PageInfo<StudentQualityScoreModel> getClassPerformanceCheckList(StudentQualityScoreQueryReqModel reqModel);

    String exportStudentQualityScoreList(StudentQualityScoreQueryReqModel reqModel);

    boolean hasPerformance(Long periodId);

    boolean canRemovePerformanceId(Long performanceId);

    void updatePerformanceById(Long performanceId, String performance);

    //获取学段的素质评价
    List<StudentQualityScoreModel> getStudentQualityScoreList(Long periodId, Long classId,Long schoolId);
}