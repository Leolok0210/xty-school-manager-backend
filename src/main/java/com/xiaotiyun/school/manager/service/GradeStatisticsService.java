package com.xiaotiyun.school.manager.service;

import com.xiaotiyun.school.manager.model.dto.StudentSubjectScoreDTO;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.ExcellentAndGoodGradesResModel;
import com.xiaotiyun.school.manager.model.res.GradeClassAvgResModel;
import com.xiaotiyun.school.manager.model.res.GradeFlunkResModel;
import com.xiaotiyun.school.manager.model.res.GradeYearResModel;
import com.xiaotiyun.school.manager.model.res.GradesStatisticsExcelResModel;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 成绩统计服务
 */
public interface GradeStatisticsService {

    List<GradeClassAvgResModel> getGradeClassAvg(GradeClassAvgReqModel reqModel);

    ResponseEntity<byte[]> exportGradeClassAvg(GradeClassAvgReqModel reqModel) throws UnsupportedEncodingException;

    List<StudentSubjectScoreDTO> getStudentSubjectScore(GetStudentSubjectScoreReqModel reqModel);

    List<GradeFlunkResModel> getGradeFlunk(GradeFlunkReqModel reqModel);

    ResponseEntity<byte[]> exportFlunkExport(GradeFlunkReqModel reqModel) throws UnsupportedEncodingException;

    ExcellentAndGoodGradesResModel getExcellentAndGood(ExcellentAndGoodGradesReqModel reqModel,long schoolId);

    GradeYearResModel getGradeYear(GradeYearReqModel reqModel);

    ResponseEntity<byte[]> exportGradeYear(GradeYearReqModel reqModel) throws UnsupportedEncodingException;

    byte[] exportExcellentAndGood(ExcellentAndGoodGradesReqModel reqModel,long schoolId);

    /**
     * 获取最高成绩统计数据
     * @param reqModel 请求参数
     * @return 最高成绩统计数据
     */
    GradesStatisticsExcelResModel getTopScore(TopScoreReqModel reqModel,long schoolId);

    /**
     * 导出最高成绩统计数据
     * @param reqModel 请求参数
     * @return Excel文件字节数组
     */
    byte[] exportTopScore(TopScoreReqModel reqModel,long schoolId);

}
