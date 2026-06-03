package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.UserClassRelEntity;
import com.xiaotiyun.school.manager.model.entity.UserSchoolRelEntity;
import com.xiaotiyun.school.manager.model.req.UserAddReqModel;
import com.xiaotiyun.school.manager.model.req.UserUpdateReqModel;

public interface UserClassRelService extends IService<UserClassRelEntity> {
    public void saveUserClass(UserAddReqModel reqModel, long schoolId, UserSchoolRelEntity userSchool);

    void updateUserClass(UserUpdateReqModel reqModel, long schoolId, UserSchoolRelEntity userSchool);
}