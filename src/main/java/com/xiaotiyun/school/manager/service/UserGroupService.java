package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.UserGroupEntity;
import com.xiaotiyun.school.manager.model.req.UserGroupAddReqModel;
import com.xiaotiyun.school.manager.model.req.UserGroupQueryReqModel;
import com.xiaotiyun.school.manager.model.res.UserGroupDetailResModel;
import com.xiaotiyun.school.manager.model.res.UserGroupResModel;

import java.util.List;

public interface UserGroupService extends IService<UserGroupEntity> {
    /**
     * 新增用户组
     */
    void addUserGroup(UserGroupAddReqModel reqModel,long schoolId);
    
    /**
     * 修改用户组
     */
    void updateUserGroup(Long id, UserGroupAddReqModel reqModel,long schoolId);
    
    /**
     * 删除用户组
     */
    void deleteUserGroup(Long id);
    
    /**
     * 查看用户组详情
     */
    UserGroupDetailResModel getUserGroupDetail(Long id,long schoolId);
    
    /**
     * 查询用户组列表
     */
    PageInfo<UserGroupDetailResModel> getUserGroupList(UserGroupQueryReqModel reqModel,long schoolId);

//    /**
//     * 判断是否是学校管理员
//     */
//    boolean isSchoolAdmin(Long userGroupId, Long schoolId);

    //获取预设用户组
    List<UserGroupEntity> getPresetUserGroup();

    boolean isSchoolAdmin(Long userGroupId);

    List<UserGroupResModel> getUserGroupAndUser(Long schoolId);
}