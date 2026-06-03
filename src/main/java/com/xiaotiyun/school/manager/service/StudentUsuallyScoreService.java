package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.dto.StudentUsuallyPartakeCountDTO;
import com.xiaotiyun.school.manager.model.dto.StudentUsuallyScoreDetailDTO;
import com.xiaotiyun.school.manager.model.entity.StudentUsuallyScoreEntity;
import com.xiaotiyun.school.manager.model.req.StudentUsuallyScorePageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentUsuallyScoreReqModel;
import com.xiaotiyun.school.manager.model.req.StudentUsuallyScoreSaveReqModel;
import com.xiaotiyun.school.manager.model.req.StudentUsuallyScoreUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.StudentUsuallyScorePageResModel;
import com.xiaotiyun.school.manager.model.res.StudentPeriodScoreResModel;
import com.xiaotiyun.school.manager.model.res.StudentUsuallyScoreResModel;

import java.util.List;

public interface StudentUsuallyScoreService extends IService<StudentUsuallyScoreEntity> {

    /**
     * 查询列表
     */
    PageInfo<StudentUsuallyScorePageResModel> page(StudentUsuallyScorePageReqModel reqModel);

    /**
     * 查询列表-学生端(非鉴权)
     */
    PageInfo<StudentUsuallyScoreResModel> pageByStudent(StudentUsuallyScoreReqModel reqModel);

    /**
     * 新增
     */
    List<StudentUsuallyScoreEntity> save(StudentUsuallyScoreSaveReqModel reqModel);

    /**
     * 修改
     */
    StudentUsuallyScoreEntity update(Long id, StudentUsuallyScoreUpdateReqModel reqModel);

    /**
     * 删除
     */
    void delete(Long id);

    /**
     * 获取参与人数
     */
    List<StudentUsuallyPartakeCountDTO> partakeCountList(List<Long> taskIds);

    /**
     * 获取参与考试列表
     */
    List<Long> partakeTaskList(String studentInfo);

    /**
     * 获取成绩列表
     */
    List<StudentUsuallyScoreDetailDTO> scoreDetailList(Long studentId, List<Long> taskIds);

    /**
     * 获取平时成绩
     */
    List<StudentPeriodScoreResModel> getUsuallyScores(Long classId, Long periodId);
}