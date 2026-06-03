package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.dto.StudentExamPartakeCountDTO;
import com.xiaotiyun.school.manager.model.dto.StudentExamScoreDetailDTO;
import com.xiaotiyun.school.manager.model.entity.StudentExamScoreEntity;
import com.xiaotiyun.school.manager.model.req.StudentExamScorePageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentExamScoreReqModel;
import com.xiaotiyun.school.manager.model.req.StudentExamScoreSaveReqModel;
import com.xiaotiyun.school.manager.model.req.StudentExamScoreUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.StudentExamScorePageResModel;
import com.xiaotiyun.school.manager.model.res.StudentExamScoreResModel;
import com.xiaotiyun.school.manager.model.res.StudentPeriodScoreResModel;

import java.util.List;

public interface StudentExamScoreService extends IService<StudentExamScoreEntity> {

    /**
     * 查询列表
     */
    PageInfo<StudentExamScorePageResModel> page(StudentExamScorePageReqModel reqModel);

    /**
     * 考试成绩查看
     */
    PageInfo<StudentExamScoreResModel> pageByStudent(StudentExamScoreReqModel reqModel);

    /**
     * 新增
     */
    List<StudentExamScoreEntity> save(StudentExamScoreSaveReqModel reqModel);

    /**
     * 修改
     */
    StudentExamScoreEntity update(Long id, StudentExamScoreUpdateReqModel reqModel);

    /**
     * 删除
     */
    void delete(Long id);

    /**
     * 获取参与人数
     */
    List<StudentExamPartakeCountDTO> partakeCountList(List<Long> taskIds);

    /**
     * 获取参与考试列表
     */
    List<Long> partakeTaskList(String studentInfo);

    /**
     * 获取成绩列表
     */
    List<StudentExamScoreDetailDTO> scoreDetailList(Long studentId, List<Long> taskIds);

    /**
     * 获取学生平时成绩列表
     * @param classId 班级ID
     * @param periodId 学段ID
     * @return 学生平时成绩列表
     */
    List<StudentPeriodScoreResModel> getStudentPeriodScores(Long classId, Long periodId);
}