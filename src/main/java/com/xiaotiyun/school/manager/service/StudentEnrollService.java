package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.StudentEnrollEntity;
import com.xiaotiyun.school.manager.model.req.StudentEnrollSaveReqModel;
import com.xiaotiyun.school.manager.model.res.StudentEnrollResModel;

public interface StudentEnrollService extends IService<StudentEnrollEntity> {

    /**
     * 获取信息
     */
    StudentEnrollResModel info(Long studentId);

    /**
     * 新增/编辑
     */
    void addOrEdit(StudentEnrollSaveReqModel reqModel);
}