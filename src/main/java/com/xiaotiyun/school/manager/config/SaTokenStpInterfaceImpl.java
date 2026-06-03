package com.xiaotiyun.school.manager.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import com.xiaotiyun.school.manager.basic.enums.UserTypeEnum;
import com.xiaotiyun.school.manager.dao.*;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.service.UserGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class SaTokenStpInterfaceImpl implements StpInterface {

    @Resource
    private UserGroupService userGroupService;

    @Resource
    private UserGroupMenuDao userGroupMenuDao;

    @Resource
    private MenuDao menuDao;

    @Resource
    private SchoolMenuDao schoolMenuDao;

    @Resource
    private UserDao userDao;

    @Resource
    private UserSchoolRelDao userSchoolRelDao;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = Long.valueOf(loginId.toString());
        Long schoolId = getLoginUserSchoolId();

        // 1. 获取用户信息，判断是否是超级管理员
        UserEntity user = userDao.selectById(userId);
        if (user != null && UserTypeEnum.isSuperAdmin(user.getUserType())) {
            List<String> permissions = new ArrayList<>();
            permissions.add("*");
            return permissions;
        }

        // 2. 从用户学校关系表表获取用户在当前学校的用户组
        Set<String> permissions = new HashSet<>();
        LambdaQueryWrapper<UserSchoolRelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserSchoolRelEntity::getUserId, userId)
            .eq(UserSchoolRelEntity::getSchoolId, schoolId);
        UserSchoolRelEntity userSchoolRel = userSchoolRelDao.selectOne(queryWrapper);
        if (userSchoolRel != null && !ObjectUtils.isEmpty(userSchoolRel.getUserGroupIds())) {
            // 3. 判断是否是学校管理员
//            boolean isSchoolAdmin = Stream.of(userSchoolRel.getUserGroupIds().split(","))
//                    .map(Long::valueOf)
//                    .anyMatch(groupId -> userGroupService.isSchoolAdmin(groupId));
//            if (isSchoolAdmin) {
//                return getSchoolAllPermissions(schoolId);
//            }

            // 4. 获取普通用户的权限
            List<Long> userGroupIds = Stream.of(userSchoolRel.getUserGroupIds().split(","))
                    .map(Long::valueOf)
                    .collect(Collectors.toList());

            //过滤出预设用户组
            List<UserGroupEntity> presetUserGroup = userGroupService.getPresetUserGroup();
            List<Long> presets = userGroupIds.stream()
                    .filter(id -> presetUserGroup.stream().map(BaseEntity::getId).anyMatch(groupId -> groupId.equals(id)))
                    .collect(Collectors.toList());
            List<Long> groupIds = userGroupIds.stream()
                    .filter(id -> presetUserGroup.stream().map(BaseEntity::getId).noneMatch(groupId -> groupId.equals(id)))
                    .collect(Collectors.toList());
            if (!presets.isEmpty()) {
                List<String> allPermissions = getSchoolAllPermissions(schoolId);
                //tomap
                if(!CollectionUtils.isEmpty(allPermissions))
                {
                    Map<String, String> allPermissionsMap = allPermissions.stream().collect(Collectors.toMap(item -> item, Function.identity(), (x1, x2) -> x1));
                    //查出权限
                    List<UserGroupMenuEntity> presetUserGroupMenus = userGroupMenuDao.selectList(
                            new LambdaQueryWrapper<UserGroupMenuEntity>()
                                    .in(UserGroupMenuEntity::getUserGroupId, presets));
                    // 获取关联的菜单权限标识
                    List<Long> menuIds = presetUserGroupMenus.stream()
                            .map(UserGroupMenuEntity::getMenuId)
                            .collect(Collectors.toList());
                    if (!menuIds.isEmpty()) {
                        List<MenuEntity> menus = menuDao.selectList(
                                new LambdaQueryWrapper<MenuEntity>().in(MenuEntity::getId, menuIds));
                        menus.forEach(menu -> {
                            if (menu.getPermission() != null && allPermissionsMap.containsKey(menu.getPermission())) {
                                permissions.add(menu.getPermission());
                            }
                        });
                    }
                }
            }

            // 5. 查询用户组关联的菜单权限
            if (!groupIds.isEmpty()) {
                List<UserGroupMenuEntity> userGroupMenus = userGroupMenuDao.selectList(
                    new LambdaQueryWrapper<UserGroupMenuEntity>()
                        .in(UserGroupMenuEntity::getUserGroupId, groupIds));

                // 6. 获取关联的菜单权限标识
                List<Long> menuIds = userGroupMenus.stream()
                    .map(UserGroupMenuEntity::getMenuId)
                    .collect(Collectors.toList());

                if (!menuIds.isEmpty()) {
                    List<MenuEntity> menus = menuDao.selectList(
                        new LambdaQueryWrapper<MenuEntity>().in(MenuEntity::getId, menuIds));
                    menus.forEach(menu -> {
                        if (menu.getPermission() != null) {
                            permissions.add(menu.getPermission());
                        }
                    });
                }
            }
        }

        return new ArrayList<>(permissions);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        UserEntity user = userDao.selectById((Serializable) loginId);
        if (user != null && UserTypeEnum.isSuperAdmin(user.getUserType())) {
            List<String> roles = new ArrayList<>();
            roles.add("role:superAdmin"); // 增加超管角色
            return roles;
        }
        return new ArrayList<>();
    }

    /**
     * 获取学校所有权限
     */
    private List<String> getSchoolAllPermissions(Long schoolId) {
        // 通过学校菜单关联表获取该学校的所有菜单ID
        List<SchoolMenuEntity> schoolMenus = schoolMenuDao.selectList(
            new LambdaQueryWrapper<SchoolMenuEntity>()
                .eq(SchoolMenuEntity::getSchoolId, schoolId));
                
        List<Long> menuIds = schoolMenus.stream()
            .map(SchoolMenuEntity::getMenuId)
            .collect(Collectors.toList());
            
        if (menuIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取这些菜单的权限标识
        List<MenuEntity> menus = menuDao.selectList(
            new LambdaQueryWrapper<MenuEntity>()
                .in(MenuEntity::getId, menuIds)
                .select(MenuEntity::getPermission));
                
        if (menus == null) {
            return new ArrayList<>();
        }

        return menus.stream()
            .filter(menu -> menu != null && menu.getPermission() != null)  // 同时检查menu对象和permission
            .map(MenuEntity::getPermission)
            .distinct()
            .collect(Collectors.toList());
    }

    /**
     * 获取当前登录用户的学校ID
     * @return 学校ID
     */
    public Long getLoginUserSchoolId() {
        try {
            String schoolId = SaHolder.getRequest().getHeader("schoolId");
            return schoolId != null ? Long.valueOf(schoolId) : null;
        } catch (Exception e) {
            log.warn("获取不到当前用户的学校ID", e);
            return null;
        }
    }
} 