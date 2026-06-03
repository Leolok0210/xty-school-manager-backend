package com.xiaotiyun.school.manager.service;

import com.xiaotiyun.school.manager.model.entity.StudentEntity;
import com.xiaotiyun.school.manager.model.entity.UserEntity;
import com.xiaotiyun.school.manager.model.req.LoginReqModel;
import com.xiaotiyun.school.manager.model.req.UpdatePasswordReqModel;
import com.xiaotiyun.school.manager.model.res.LoginResModel;
import com.xiaotiyun.school.manager.model.res.MenuResModel;

import java.util.List;

/**
 * 认证服务接口
 */
public interface AuthService {
    /**
     * 登录
     * 
     * @param reqModel 登录请求
     * @return 登录响应
     */
    LoginResModel login(LoginReqModel reqModel);

    // 登录用户
    String loginByUser(UserEntity user);

    // 登录用户
    String loginByStudent(StudentEntity student);

    LoginResModel wxGetLoginData();

    /**
     * 登出
     */
    void logout();

    /**
     * 修改密码
     * 
     * @param reqModel 修改密码请求
     */
    void updatePassword(UpdatePasswordReqModel reqModel);

    /**
     * 获取用户菜单
     */
    List<MenuResModel> getUserMenu(long schoolId);
} 