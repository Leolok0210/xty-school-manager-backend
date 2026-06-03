package com.xiaotiyun.school.manager.service;

import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.StudentQualityScore;
import com.xiaotiyun.school.manager.model.req.StudentQualityScoreQueryReqModel;
import com.xiaotiyun.school.manager.model.res.StudentQualityScoreListResModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudentQualityScoreService {
    void createStudentQualityScores(List<StudentQualityScore> studentQualityScores);
    void updateStudentQualityScore(StudentQualityScore studentQualityScore);
    void deleteStudentQualityScore(Long id);
    StudentQualityScore getStudentQualityScoreById(Long id);
    PageInfo<StudentQualityScoreListResModel> getStudentQualityScoreList(StudentQualityScoreQueryReqModel reqModel);

    List<StudentQualityScoreListResModel> getStudentQualityScoreExportList(StudentQualityScoreQueryReqModel reqModel);

    Long importStudentQualityScore(MultipartFile file, Long schoolId, Long classId, Long term,String sid);

    /**
     * 查询学生这个学段是否有学生素质评分记录
     */
    boolean hasScore(Long periodId);


}