package com.xiaotiyun.school.manager.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.constant.NumberConstant;
import com.xiaotiyun.school.manager.basic.enums.UserGroupTypeEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.dao.UserGroupDao;
import com.xiaotiyun.school.manager.dao.UserGroupMenuDao;
import com.xiaotiyun.school.manager.dao.UserSchoolRelDao;
import com.xiaotiyun.school.manager.model.entity.UserGroupEntity;
import com.xiaotiyun.school.manager.model.entity.UserGroupMenuEntity;
import com.xiaotiyun.school.manager.model.entity.UserSchoolRelEntity;
import com.xiaotiyun.school.manager.model.req.UserGroupAddReqModel;
import com.xiaotiyun.school.manager.model.req.UserGroupQueryReqModel;
import com.xiaotiyun.school.manager.model.res.UserGroupDetailResModel;
import com.xiaotiyun.school.manager.model.res.UserGroupResModel;
import com.xiaotiyun.school.manager.model.res.UserSchoolRelResModel;
import com.xiaotiyun.school.manager.service.UserGroupService;
import com.xiaotiyun.school.manager.service.UserSchoolRelService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserGroupServiceImpl extends ServiceImpl<UserGroupDao, UserGroupEntity> implements UserGroupService {

    @Resource
    private UserGroupMenuDao userGroupMenuDao;

    @Resource
    private UserSchoolRelDao userSchoolRelDao;
    @Resource
    private UserSchoolRelService userSchoolRelService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUserGroup(UserGroupAddReqModel reqModel, long schoolId) {
        // 1. 校验用户组名称是否重复
        if (isUserGroupNameExists(reqModel.getName(), schoolId, null)) {
            throw new BusinessException(LanguageConstants.USER_GROUP_NAME_EXISTS);
        }

        // 2. 保存用户组信息
        UserGroupEntity userGroup = new UserGroupEntity();
        BeanUtils.copyProperties(reqModel, userGroup);
        userGroup.setSchoolId(schoolId);
        save(userGroup);

        // 3. 保存用户组菜单关联
        List<UserGroupMenuEntity> menuList = reqModel.getMenuIds().stream().map(menuId -> {
            UserGroupMenuEntity menu = new UserGroupMenuEntity();
            menu.setUserGroupId(userGroup.getId());
            menu.setMenuId(menuId);
            return menu;
        }).collect(Collectors.toList());
        userGroupMenuDao.insertBatch(menuList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserGroup(Long id, UserGroupAddReqModel reqModel, long schoolId) {
        // 1. 校验是否是预设用户组
        UserGroupEntity userGroup = getById(id);
        if (userGroup == null) {
            throw new BusinessException(LanguageConstants.USER_GROUP_NOT_EXISTS);
        }
        if (isPresetUserGroup(userGroup.getId()) && !StpUtil.getPermissionList().contains("*")) {
            throw new BusinessException(LanguageConstants.PRESET_USER_GROUP_NO_MODIFY);
        }
//        if (isPresetUserGroup(userGroup.getId()) && userGroup.getSchoolId() == null) {
//            throw new BusinessException(LanguageConstants.PRESET_USER_GROUP_NO_MODIFY);
//        }

        // 2. 校验用户组名称是否重复
        if (isUserGroupNameExists(reqModel.getName(), schoolId, id)) {
            throw new BusinessException(LanguageConstants.USER_GROUP_NAME_EXISTS);
        }

        // 3. 更新用户组信息
        userGroup = new UserGroupEntity();
        BeanUtils.copyProperties(reqModel, userGroup);
        userGroup.setId(id);
        updateById(userGroup);

        // 4. 更新用户组菜单关联
        userGroupMenuDao.delete(new LambdaQueryWrapper<UserGroupMenuEntity>()
                .eq(UserGroupMenuEntity::getUserGroupId, id));
        List<UserGroupMenuEntity> menuList = reqModel.getMenuIds().stream().map(menuId -> {
            UserGroupMenuEntity menu = new UserGroupMenuEntity();
            menu.setUserGroupId(id);
            menu.setMenuId(menuId);
            return menu;
        }).collect(Collectors.toList());
        userGroupMenuDao.insertBatch(menuList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserGroup(Long id) {
        // 1. 校验是否是预设用户组
        UserGroupEntity userGroup = getById(id);
        if (userGroup == null) {
            throw new BusinessException(LanguageConstants.USER_GROUP_NOT_EXISTS);
        }
        if (isPresetUserGroup(userGroup.getId()) && !StpUtil.getPermissionList().contains("*")) {
            throw new BusinessException(LanguageConstants.PRESET_USER_GROUP_NO_MODIFY);
        }
//        if (isPresetUserGroup(userGroup.getCode())  && userGroup.getSchoolId() == null) {
//            throw new BusinessException(LanguageConstants.PRESET_USER_GROUP_NO_DELETE);
//        }

        // 2. 校验是否有关联用户
        Long userCount = userSchoolRelDao.selectCount(new QueryWrapper<UserSchoolRelEntity>()
                .apply("FIND_IN_SET({0}, user_group_ids)", id)
                .eq("deleted", NumberConstant.ZERO));
        if (userCount > 0) {
            throw new BusinessException(LanguageConstants.USER_GROUP_IN_USE);
        }

        // 3. 删除用户组
        removeById(id);

        // 4. 删除用户组菜单关联
        userGroupMenuDao.delete(new LambdaQueryWrapper<UserGroupMenuEntity>()
                .eq(UserGroupMenuEntity::getUserGroupId, id));
    }

    @Override
    public UserGroupDetailResModel getUserGroupDetail(Long id, long schoolId) {
        // 1. 获取用户组信息
        UserGroupEntity userGroup = getById(id);
        if (userGroup == null) {
            return null;
        }

        // 2. 获取用户组菜单关联
        List<UserGroupMenuEntity> menuList = userGroupMenuDao.selectList(new LambdaQueryWrapper<UserGroupMenuEntity>()
                .eq(UserGroupMenuEntity::getUserGroupId, id));

        // 3. 获取关联用户数
        Long userCount = userSchoolRelDao.selectCount(new QueryWrapper<UserSchoolRelEntity>()
                .apply("FIND_IN_SET({0}, user_group_ids)", id)
                .eq("school_id", schoolId)
                .eq("deleted", NumberConstant.ZERO));

        // 4. 组装返回数据
        UserGroupDetailResModel resModel = new UserGroupDetailResModel();
        BeanUtils.copyProperties(userGroup, resModel);
        // 添加是否预设用户组
        resModel.setPreset(isPresetUserGroup(userGroup.getId()));
        resModel.setMenuIds(menuList.stream().map(UserGroupMenuEntity::getMenuId).collect(Collectors.toList()));
        resModel.setUserCount(userCount);
        return resModel;
    }

    @Override
    public PageInfo<UserGroupDetailResModel> getUserGroupList(UserGroupQueryReqModel reqModel, long schoolId) {
        // 1. 构建查询条件
        LambdaQueryWrapper<UserGroupEntity> wrapper = new LambdaQueryWrapper<UserGroupEntity>()
                .like(StringUtils.isNotBlank(reqModel.getName()), UserGroupEntity::getName, reqModel.getName())
                .and(w -> w.eq(schoolId > 0, UserGroupEntity::getSchoolId, schoolId)
                        .or(w1 -> w1.eq(UserGroupEntity::getSchoolId, 0)));


        // 2. 开启分页并查询数据
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<UserGroupEntity> list = list(wrapper);
        PageInfo<UserGroupEntity> pageInfo = new PageInfo<>(list);

        // 3. 转换返回结果
        List<UserGroupDetailResModel> resList = list.stream().map(userGroup -> {
            UserGroupDetailResModel resModel = new UserGroupDetailResModel();
            BeanUtils.copyProperties(userGroup, resModel);
            resModel.setUserCount(userSchoolRelDao.selectCount(new QueryWrapper<UserSchoolRelEntity>()
                    .apply("FIND_IN_SET({0}, user_group_ids)", userGroup.getId())
                    .eq("school_id", schoolId)
                    .eq("deleted", NumberConstant.ZERO)));
            resModel.setPreset(isPresetUserGroup(userGroup.getId()));
            return resModel;
        }).collect(Collectors.toList());

        // 4. 创建新的分页信息对象并设置分页相关属性
        PageInfo<UserGroupDetailResModel> result = new PageInfo<>(resList);
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());

        return result;
    }

    /**
     * 校验用户组名称是否存在
     */
    private boolean isUserGroupNameExists(String name, Long schoolId, Long excludeId) {
        LambdaQueryWrapper<UserGroupEntity> wrapper = new LambdaQueryWrapper<UserGroupEntity>()
                .eq(UserGroupEntity::getName, name)
                .eq(UserGroupEntity::getSchoolId, schoolId);
        if (excludeId != null) {
            wrapper.ne(UserGroupEntity::getId, excludeId);
        }
        return count(wrapper) > 0;
    }

    /**
     * 是否是预设用户组
     */
    private boolean isPresetUserGroup(Long id) {
        UserGroupEntity groupEntity = getById(id);
        return groupEntity.getSchoolId() == null || groupEntity.getSchoolId() == 0;
    }


    @Override
    public boolean isSchoolAdmin(Long userGroupId) {
        UserGroupEntity userGroup = getById(userGroupId);
        return userGroup != null
                && UserGroupTypeEnum.isSchoolAdmin(userGroup.getCode());
    }

    @Override
    public List<UserGroupEntity> getPresetUserGroup() {
        return list(new LambdaQueryWrapper<UserGroupEntity>()
                .eq(UserGroupEntity::getSchoolId, 0));
    }

    @Override
    public List<UserGroupResModel> getUserGroupAndUser(Long schoolId) {
        List<UserGroupResModel> result = new ArrayList<>();
        if (schoolId == null || schoolId <= 0) {
            return result;
        }
        // 1. 查询符合条件的角色（当前学校ID + 预设角色）
        LambdaQueryWrapper<UserGroupEntity> wrapper = new LambdaQueryWrapper<UserGroupEntity>()
                .and(w -> w.eq(UserGroupEntity::getSchoolId, schoolId)
                        .or(w1 -> w1.eq(UserGroupEntity::getSchoolId, 0)));

        List<UserGroupEntity> userGroups = this.list(wrapper);
        if (CollectionUtils.isEmpty(userGroups)) {
            return result;
        }
        // 2. 查询该学校下的所有用户
        QueryWrapper<UserSchoolRelEntity> userQuery = new QueryWrapper<>();
        userQuery.lambda().eq(UserSchoolRelEntity::getSchoolId, schoolId);
        List<UserSchoolRelEntity> userList = userSchoolRelService.list(userQuery);

        // 3. 构建角色ID到用户的映射关系
        Map<Long, List<UserSchoolRelEntity>> groupUserMap = new HashMap<>();
        userGroups.forEach(group -> {
            groupUserMap.put(group.getId(), new ArrayList<>());
        });

        // 4. 将用户分配到对应的角色分组中
        for (UserSchoolRelEntity user : userList) {
            if (StringUtils.isBlank(user.getUserGroupIds())) {
                continue;
            }
            Arrays.stream(user.getUserGroupIds().split(","))
                    .map(Long::parseLong)
                    .filter(groupUserMap::containsKey)
                    .forEach(groupId -> groupUserMap.get(groupId).add(user));
        }
        // 5. 组装返回结果
        return userGroups.stream().map(group -> {
            UserGroupResModel model = new UserGroupResModel();
            BeanUtils.copyProperties(group, model);
            // 设置关联用户列表（可根据需要调整返回的用户信息字段）
            model.setUserList(groupUserMap.getOrDefault(group.getId(), Collections.emptyList())
                    .stream()
                    .map(user -> {
                        // 这里可以自定义需要返回的用户信息
                        UserSchoolRelResModel userModel = new UserSchoolRelResModel();
                        userModel.setUserId(user.getUserId());
                        userModel.setUsername(user.getUsername());
                        return userModel;
                    }).collect(Collectors.toList()));
            return model;
        }).collect(Collectors.toList());
    }

} 