package com.xiaotiyun.school.manager.service;

import com.xiaotiyun.school.manager.model.dto.StudentScoreCheckDTO;
import com.xiaotiyun.school.manager.model.req.ClassTopStudentsReqModel;
import com.xiaotiyun.school.manager.model.req.StudentSubjectScoresReqModel;
import com.xiaotiyun.school.manager.model.req.TranScriptGenerateReqModel;
import com.xiaotiyun.school.manager.model.req.YearGradeCheckReqModel;
import com.xiaotiyun.school.manager.model.res.*;

import java.util.List;

public interface StudentScoreCheckService {

    List<ClassTopStudentsResModel> getClassTopStudents(ClassTopStudentsReqModel reqModel);

    List<StudentSubjectScoresResModel> getStudentSubjectScores(StudentSubjectScoresReqModel reqModel);

    List<YearGradeCheckResModel> getYearGradeCheck(YearGradeCheckReqModel reqModel);

    byte[] getExportYearGradeCheck(YearGradeCheckReqModel reqModel, String fileName);

    /**
     * 导出各班名列前茅名单
     * 
     * @param reqModel 请求参数
     */
    byte[] exportClassTopStudents(ClassTopStudentsReqModel reqModel);

    /**
     * 导出学生各科成绩（学段成绩）
     * 
     * @param reqModel 请求参数
     * @return 导出的Excel文件字节数组
     */
    byte[] exportStudentSubjectScores(StudentSubjectScoresReqModel reqModel);
}