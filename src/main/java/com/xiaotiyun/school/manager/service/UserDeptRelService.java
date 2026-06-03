package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.UserDeptRelEntity;
import com.xiaotiyun.school.manager.model.entity.UserSchoolRelEntity;
import com.xiaotiyun.school.manager.model.req.UserAddReqModel;
import com.xiaotiyun.school.manager.model.req.UserUpdateReqModel;

/**
 * 用户部门关联表 Service 接口
 */
public interface UserDeptRelService extends IService<UserDeptRelEntity> {

    void saveUserDept(UserAddReqModel reqModel, long schoolId, UserSchoolRelEntity userSchool);

    void updateUserDept(UserUpdateReqModel reqModel, long schoolId, UserSchoolRelEntity userSchool);
}