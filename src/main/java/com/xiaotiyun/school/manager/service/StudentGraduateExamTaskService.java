package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.StudentGraduateExamTaskEntity;
import com.xiaotiyun.school.manager.model.req.StudentGraduateExamPageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentGraduateExamScoreSaveReqModel;
import com.xiaotiyun.school.manager.model.req.StudentGraduateExamTaskSaveReqModel;
import com.xiaotiyun.school.manager.model.req.StudentScoreReqModel;
import com.xiaotiyun.school.manager.model.res.StudentGraduateExamTaskPageResModel;
import com.xiaotiyun.school.manager.model.res.StudentGraduateExamTaskResModel;
import com.xiaotiyun.school.manager.model.res.StudentScorePageResModel;

import java.util.List;

public interface StudentGraduateExamTaskService extends IService<StudentGraduateExamTaskEntity> {

    /**
     * 分页查询列表
     */
    PageInfo<StudentGraduateExamTaskPageResModel> page(StudentGraduateExamPageReqModel reqModel);

    /**
     * 新增
     */
    StudentGraduateExamTaskEntity save(StudentGraduateExamTaskSaveReqModel reqModel);

    /**
     * 修改
     */
    StudentGraduateExamTaskEntity update(Long id, StudentGraduateExamTaskSaveReqModel reqModel);

    /**
     * 获取
     */
    StudentGraduateExamTaskResModel info(Long id);

    /**
     * 删除
     */
    void delete(Long id);

    /**
     * 查询学生成绩
     */
    List<StudentScorePageResModel> studentScore(StudentScoreReqModel reqModel);

    /**
     * 录入成绩
     */
    void scoreAdd(StudentGraduateExamScoreSaveReqModel reqModel);


    /**
     * 查询是否存在毕业成绩
     */
    boolean hasScore(Long periodId);
}