package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.dto.StudentGraduateExamPartakeCountDTO;
import com.xiaotiyun.school.manager.model.dto.StudentGraduateExamScoreDetailDTO;
import com.xiaotiyun.school.manager.model.entity.StudentGraduateExamScoreEntity;
import com.xiaotiyun.school.manager.model.req.StudentGraduateExamScorePageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentGraduateExamScoreSaveReqModel;
import com.xiaotiyun.school.manager.model.req.StudentGraduateExamScoreUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.StudentGraduateExamScorePageResModel;

import java.util.List;

public interface StudentGraduateExamScoreService extends IService<StudentGraduateExamScoreEntity> {

    /**
     * 查询列表
     */
    PageInfo<StudentGraduateExamScorePageResModel> page(StudentGraduateExamScorePageReqModel reqModel);

    /**
     * 新增
     */
    List<StudentGraduateExamScoreEntity> save(StudentGraduateExamScoreSaveReqModel reqModel);

    /**
     * 修改
     */
    StudentGraduateExamScoreEntity update(Long id, StudentGraduateExamScoreUpdateReqModel reqModel);

    /**
     * 删除
     */
    void delete(Long id);

    /**
     * 获取参与人数
     */
    List<StudentGraduateExamPartakeCountDTO> partakeCountList(List<Long> taskIds);

    /**
     * 获取参与考试列表
     */
    List<Long> partakeTaskList(String studentInfo);

    /**
     * 获取成绩列表
     */
    List<StudentGraduateExamScoreDetailDTO> scoreDetailList(Long studentId, List<Long> taskIds);
}