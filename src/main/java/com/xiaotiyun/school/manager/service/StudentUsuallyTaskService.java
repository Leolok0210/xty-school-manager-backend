package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.StudentUsuallyTaskEntity;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;

import java.util.List;

public interface StudentUsuallyTaskService extends IService<StudentUsuallyTaskEntity> {

    /**
     * 分页查询平时分登记列表
     */
    PageInfo<StudentUsuallyTaskPageResModel> page(StudentUsuallyPageReqModel reqModel);

    /**
     * 新增
     */
    StudentUsuallyTaskEntity save(StudentUsuallyTaskSaveReqModel reqModel);

    /**
     * 修改
     */
    StudentUsuallyTaskEntity update(Long id, StudentUsuallyTaskSaveReqModel reqModel);

    /**
     * 获取
     */
    StudentUsuallyTaskResModel info(Long id);

    /**
     * 删除
     */
    void delete(Long id);

    /**
     * 检查讯息
     */
    String check(Long schoolId, StudentUsuallyTaskCheckReqModel reqModel);

    /**
     * 查询列表
     */
    List<StudentUsuallyScoreCheckResModel> scoreCheck(StudentUsuallyScoreCheckReqModel reqModel);

    /**
     * 查询列表
     */
    List<StudentUsuallyScoreAnalysisResModel> scoreAnalysis(StudentUsuallyScoreAnalysisReqModel reqModel);

    /**
     * 查询学生成绩
     */
    List<StudentScorePageResModel> studentScore(StudentScoreReqModel reqModel);

    /**
     * 查询是否存在平时分
     */
    boolean hasScore(Long periodId);

    boolean checkTask(Long schoolId);
}