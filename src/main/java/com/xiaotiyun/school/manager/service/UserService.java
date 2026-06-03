package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.entity.UserEntity;
import com.xiaotiyun.school.manager.model.req.UserAddReqModel;
import com.xiaotiyun.school.manager.model.req.UserDeleteReqModel;
import com.xiaotiyun.school.manager.model.req.UserQueryReqModel;
import com.xiaotiyun.school.manager.model.req.UserUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.TeacherListResModel;
import com.xiaotiyun.school.manager.model.res.UserDetailResModel;
import com.xiaotiyun.school.manager.model.res.UserMobileCheckResModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService extends IService<UserEntity> {
    /**
     * 新增用户
     */
    void addUser(UserAddReqModel reqModel, long schoolId);

    /**
     * 修改用户
     */
    void updateUser(UserUpdateReqModel reqModel, long schoolId);

    /**
     * 删除用户
     *
     * @return
     */
    Result<Integer> deleteUser(UserDeleteReqModel reqModel);

    /**
     * 查看用户详情
     */
    UserDetailResModel getUserDetail(Long id, Long schoolId);


    /**
     * 查看用户角色（班级权限校验使用）
     */
    Integer getUserRole(Long id, Long schoolId);

    /**
     * 查看用户详情
     */
    UserDetailResModel getUserDetail(Long id);

    /**
     * 查询用户列表
     */
    PageInfo<UserDetailResModel> getUserPage(UserQueryReqModel reqModel, Long schoolId);

    /**
     * 重置密码
     */
    void resetPassword(Long id);

    /**
     * 检查手机号是否已存在
     */
    boolean isMobileExists(String mobile);

    /**
     * 检查手机号是否已存在
     */
    UserMobileCheckResModel checkMobileExists(String mobile, Long schoolId);

    List<TeacherListResModel> getTeachersBySchool(Long schoolId);

    /**
     * 导入用户
     */
    Long importUser(Long schoolId, MultipartFile file);

    /**
     * 导出用户
     */
    String exportUser(Long schoolId, UserQueryReqModel reqModel);
}