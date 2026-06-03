package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.StudentGraduateEnrollEntity;
import com.xiaotiyun.school.manager.model.req.StudentGraduateEnrollBatcheSaveReqModel;
import com.xiaotiyun.school.manager.model.req.StudentGraduateEnrollPageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentGraduateEnrollSaveReqModel;
import com.xiaotiyun.school.manager.model.res.StudentGraduateEnrollPageResModel;
import com.xiaotiyun.school.manager.model.res.StudentGraduateEnrollResModel;

import java.util.List;

public interface StudentGraduateEnrollService extends IService<StudentGraduateEnrollEntity> {

    /**
     * 分页查询列表
     */
    PageInfo<StudentGraduateEnrollPageResModel> page(StudentGraduateEnrollPageReqModel reqModel);

    /**
     * 新增
     */
    void save(StudentGraduateEnrollSaveReqModel reqModel);

    /**
     * 批量新增
     */
    void batchSave(StudentGraduateEnrollBatcheSaveReqModel reqModel);

    /**
     * 已登记的学生列表
     */
    List<Long> studentList(Long classId);

    /**
     * 修改
     */
    void update(Long id, StudentGraduateEnrollSaveReqModel reqModel);

    /**
     * 获取
     */
    StudentGraduateEnrollResModel info(Long id);

    /**
     * 删除
     */
    void delete(Long id);
}