package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.entity.StudentParentEntity;
import com.xiaotiyun.school.manager.model.req.StudentParentAddReqModel;
import com.xiaotiyun.school.manager.model.res.StudentParentResModel;

import java.util.List;

/**
 * 学生家长信息Service接口
 */
public interface StudentParentService extends IService<StudentParentEntity> {

    Result addOrUpdate(List<StudentParentAddReqModel> reqModels, Long schoolId, String schoolYear);

    Result<List<StudentParentResModel>> listByStudentId(Long studentId, Long schoolId);


    List<StudentParentResModel> listByStudentIds(List<Long> studentIds, Long schoolId);
}
