package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.StudentFamilyEntity;
import com.xiaotiyun.school.manager.model.req.StudentFamilySaveReqModel;
import com.xiaotiyun.school.manager.model.req.StudentHealthDeclareAddReqModel;
import com.xiaotiyun.school.manager.model.res.StudentFamilyResModel;

public interface StudentFamilyService extends IService<StudentFamilyEntity> {
    
    /**
     * 获取学生家庭信息
     */
    StudentFamilyResModel getByStudentId(Long studentId);
    
    /**
     * 保存学生家庭信息
     */
    void save(Long studentId, StudentFamilySaveReqModel reqModel);
    
    /**
     * 修改学生家庭信息
     */
    void update(Long studentId, StudentFamilySaveReqModel reqModel);

    void updateByHealthDeclare(StudentHealthDeclareAddReqModel reqModel);
}