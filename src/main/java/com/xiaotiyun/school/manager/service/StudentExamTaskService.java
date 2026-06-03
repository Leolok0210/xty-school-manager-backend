package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.StudentExamTaskEntity;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;

import java.util.List;

public interface StudentExamTaskService extends IService<StudentExamTaskEntity> {

    /**
     * 分页查询列表
     */
    PageInfo<StudentExamTaskPageResModel> page(StudentExamPageReqModel reqModel);

    /**
     * 新增
     */
    StudentExamTaskEntity save(StudentExamTaskSaveReqModel reqModel);

    /**
     * 修改
     */
    StudentExamTaskEntity update(Long id, StudentExamTaskSaveReqModel reqModel);

    /**
     * 获取
     */
    StudentExamTaskResModel info(Long id);

    /**
     * 删除
     */
    void delete(Long id);

    /**
     * 检查讯息
     */
    String check(Long schoolId, StudentExamTaskCheckReqModel reqModel);

    /**
     * 查询列表
     */
    List<StudentExamScoreCheckResModel> scoreCheck(StudentExamScoreCheckReqModel reqModel);

    /**
     * 查询列表
     */
    List<StudentExamScoreAnalysisResModel> scoreAnalysis(StudentExamScoreAnalysisReqModel reqModel);

    /**
     * 查询学生成绩
     */
    List<StudentScorePageResModel> studentScore(StudentScoreReqModel reqModel);

    /**
     * 录入成绩
     */
    void scoreAdd(StudentExamScoreSaveReqModel reqModel);

    /**
     * 查询学段下是否有成绩
     *
     * @param id
     * @return boolean
     */
    boolean hasScore(Long id);

}