package com.xiaotiyun.school.manager.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.constant.NumberConstant;
import com.xiaotiyun.school.manager.basic.enums.UserTypeEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.dao.*;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.req.LoginReqModel;
import com.xiaotiyun.school.manager.model.req.UpdatePasswordReqModel;
import com.xiaotiyun.school.manager.model.res.LoginResModel;
import com.xiaotiyun.school.manager.model.res.MenuResModel;
import com.xiaotiyun.school.manager.model.res.SchoolInfoResModel;
import com.xiaotiyun.school.manager.model.res.UserDetailResModel;
import com.xiaotiyun.school.manager.service.AuthService;
import com.xiaotiyun.school.manager.service.MenuService;
import com.xiaotiyun.school.manager.service.UserGroupService;
import com.xiaotiyun.school.manager.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private UserDao userDao;

    @Resource
    private UserGroupMenuDao userGroupMenuDao;

    @Resource
    private MenuService menuService;

    @Resource
    private UserService userService;

    @Resource
    private UserSchoolRelDao userSchoolRelDao;

    @Resource
    private SchoolServiceImpl schoolService;
    @Resource
    private UserGroupService userGroupService;

    @Resource
    private SchoolMenuDao schoolMenuDao;


    @Resource
    private MenuDao menuDao;

    @Override
    public LoginResModel login(LoginReqModel reqModel) {
        // 1. 校验用户
        UserEntity user = userDao.selectOne(
            new LambdaQueryWrapper<UserEntity>()
                    .eq(UserEntity::getMobile, reqModel.getAccount())
                    .or()
                    .eq(UserEntity::getLoginName, reqModel.getAccount())
        );
        if (user == null) {
            throw new BusinessException(LanguageConstants.ACCOUNT_NOT_EXIST);
        }

// 2. 校驗密碼
        String inputPassword = reqModel.getPassword();
        String storedPassword = user.getPassword();
        boolean matched = false;

        // 先嘗試 BCrypt（適用於 updatePassword 使用 BCrypt 存儲的密碼）
        if (storedPassword != null && storedPassword.startsWith("$2")) {
            matched = BCrypt.checkpw(inputPassword, storedPassword);
        } else {
            // 兼容 SHA256 格式或其他舊格式密碼
            matched = inputPassword.equals(storedPassword);
            // 兼容前端 SHA256(salt+password) 加密
            if (!matched) {
                try {
                    java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
                    byte[] hash = md.digest(("salt_mBDwFRq_" + storedPassword).getBytes(java.nio.charset.StandardCharsets.UTF_8));
                    StringBuilder hex = new StringBuilder();
                    for (byte b : hash) hex.append(String.format("%02x", b));
                    matched = inputPassword.equals(hex.toString());
                } catch (Exception ignored) { }
            }
        }
        if (!matched) {
            throw new BusinessException(LanguageConstants.ACCOUNT_OR_PASSWORD_ERROR);
        }

        // 生成token
        StpUtil.login(user.getId());
        // 保存用户信息
        StpUtil.getSession().set("userInfo", user);

        return getUserLoginData(user);
    }

    @Override
    // 登录用户
    public String loginByUser(UserEntity user){
        // 生成token
        StpUtil.login(user.getId());
        // 保存用户信息
        StpUtil.getSession().set("userInfo", user);
        return StpUtil.getTokenValue();
    }

    @Override
    // 登录用户
    public String loginByStudent(StudentEntity student){
        // 生成token
        StpUtil.login("wxstu"+student.getId());
        // 保存用户信息
        StpUtil.getSession().set("student", student);
        return StpUtil.getTokenValue();
    }

    @Override
    public LoginResModel wxGetLoginData() {
        UserEntity userInfo = (UserEntity)StpUtil.getSession().get("userInfo");
        if (userInfo == null) {
            throw new BusinessException(LanguageConstants.SYSTEM_TOKEN_INVALID);
        }
        return getUserLoginData(userInfo);
    }

    @Override
    public void logout() {
        // 注销登录，不需要物理删除token，sa-token会自动处理
        StpUtil.logout();
    }

    @Override
    public void updatePassword(UpdatePasswordReqModel reqModel) {
        // 获取当前用户
        Long userId = StpUtil.getLoginIdAsLong();
        UserEntity user = userDao.selectById(userId);
        if (user == null) {
            throw new BusinessException(LanguageConstants.USER_NOT_EXIST);
        }

        // 校验旧密码
        if (user.getNeedResetPwd() == 0) {
            // 非默认密码需要校验旧密码
            if (reqModel.getOldPassword() == null) {
                throw new BusinessException(LanguageConstants.OLD_PASSWORD_REQUIRED);
            }
            if (!BCrypt.checkpw(reqModel.getOldPassword(), user.getPassword())) {
                throw new BusinessException(LanguageConstants.OLD_PASSWORD_ERROR);
            }
        }

        // 更新密码
        UserEntity updateUser = new UserEntity();
        updateUser.setId(userId);
        updateUser.setPassword(BCrypt.hashpw(reqModel.getNewPassword(), BCrypt.gensalt()));
        updateUser.setNeedResetPwd(NumberConstant.ZERO);
        userDao.updateById(updateUser);
    }

    @Override
    public List<MenuResModel> getUserMenu(long schoolId) {
        // 1. 获取当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();

        // 2. 获取用户信息
        UserEntity user = userDao.selectById(userId);
        if (user == null) {
            return new ArrayList<>();
        }

        // 3. 如果是超级管理员，返回所有有效菜单
        if (UserTypeEnum.isSuperAdmin(user.getUserType())) {
            List<MenuEntity> allMenus = menuService.list(
                    new LambdaQueryWrapper<MenuEntity>()
                            .orderByAsc(MenuEntity::getSort));
            return menuService.buildMenuTree(allMenus);
        }else{
            // 4. 普通用户获取指定学校的用户组菜单
            UserSchoolRelEntity userSchoolRel = userSchoolRelDao.selectOne(
                    new LambdaQueryWrapper<UserSchoolRelEntity>()
                            .eq(UserSchoolRelEntity::getUserId, userId)
                            .eq(UserSchoolRelEntity::getSchoolId, schoolId));

            if (userSchoolRel == null || userSchoolRel == null || ObjectUtils.isEmpty(userSchoolRel.getUserGroupIds())) {
                return new ArrayList<>();
            }
            List<MenuEntity> resultMenus = new ArrayList<>();
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
                List<Long> allPermissions = getSchoolAllPermissions(schoolId);
                //tomap
                if(!CollectionUtils.isEmpty(allPermissions))
                {
                    Map<Long, Long> allPermissionsMap = allPermissions.stream().collect(Collectors.toMap(item -> item, Function.identity(), (x1, x2) -> x1));
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
                        for (MenuEntity menu : menus) {
                            if (allPermissionsMap.containsKey(menu.getId())) {
                                resultMenus.add(menu);
                            }
                        }
                    }
                }
            }

            if(!CollectionUtils.isEmpty(groupIds))
            {
                // 5. 获取用户组关联的菜单
                List<UserGroupMenuEntity> menuList = userGroupMenuDao.selectList(
                        new LambdaQueryWrapper<UserGroupMenuEntity>()
                                .in(UserGroupMenuEntity::getUserGroupId, groupIds));

                if (!CollectionUtils.isEmpty(menuList)) {
                    // 6. 获取菜单信息
                    List<Long> menuIds = menuList.stream()
                            .map(UserGroupMenuEntity::getMenuId)
                            .distinct()
                            .collect(Collectors.toList());

                    List<MenuEntity> menus = menuService.list(
                            new LambdaQueryWrapper<MenuEntity>()
                                    .in(MenuEntity::getId, menuIds)
                                    .orderByAsc(MenuEntity::getSort));
                    resultMenus.addAll(menus);
                }
            }
            // 去重
            resultMenus = resultMenus.stream().distinct().collect(Collectors.toList());
            resultMenus.sort(Comparator.comparing(MenuEntity::getSort));
            // 7. 构建菜单树
            return menuService.buildMenuTree(resultMenus);
        }
    }

    private LoginResModel getUserLoginData(UserEntity user) {
        // 返回登录信
        LoginResModel resModel = new LoginResModel();
        resModel.setToken(StpUtil.getTokenValue());
        resModel.setNeedResetPwd(user.getNeedResetPwd());

        // 如果是超管，获取所有学校
        if(UserTypeEnum.isSuperAdmin(user.getUserType())){
            List<SchoolEntity> schoolList = schoolService.list();
            // 组装成List<SchoolInfoResModel>
            List<SchoolInfoResModel> schoolInfoList = schoolList.stream().map(school -> {
                SchoolInfoResModel schoolInfo = new SchoolInfoResModel();
                schoolInfo.setSchoolId(school.getId());
                schoolInfo.setSchoolName(school.getName());
                return schoolInfo;
            }).collect(Collectors.toList());
            resModel.setSchoolInfoList(schoolInfoList);

            UserDetailResModel userInfo = userService.getUserDetail(user.getId());
            resModel.setUserDetailResModel(userInfo);

        }else{
            // 先获取用户的所有学校信息
            List<UserSchoolRelEntity> userSchoolRelList = userSchoolRelDao.selectList(
                    new LambdaQueryWrapper<UserSchoolRelEntity>()
                            .eq(UserSchoolRelEntity::getUserId, user.getId())
            );
            if(!ObjectUtils.isEmpty(userSchoolRelList)){
                UserDetailResModel userInfo = userService.getUserDetail(user.getId(),userSchoolRelList.get(0).getSchoolId());
                resModel.setUserDetailResModel(userInfo);
            }

            // 创建一个schoolAndUserInfoList
            List<SchoolInfoResModel> schoolAndUserInfoList = new ArrayList<>();

            // 批量获取所有学校信息，避免N+1查询
            List<Long> schoolIds = userSchoolRelList.stream()
                    .map(UserSchoolRelEntity::getSchoolId)
                    .distinct()
                    .collect(Collectors.toList());
            Map<Long, SchoolEntity> schoolMap = schoolService.listByIds(schoolIds).stream()
                    .collect(Collectors.toMap(SchoolEntity::getId, Function.identity()));

            // 循环userSchoolRelList，获取每个学校的用户信息，并且组装到res中
            for (UserSchoolRelEntity userSchoolRel : userSchoolRelList) {

                SchoolInfoResModel schoolInfo = new SchoolInfoResModel();
                schoolInfo.setSchoolId(userSchoolRel.getSchoolId());

                // 获取学校详细信息
                SchoolEntity school = schoolMap.get(userSchoolRel.getSchoolId());
                if(school != null){
                    schoolInfo.setSchoolName(school.getName());
                    // 计算剩余天数
                    if (school.getExpireTime() == null) {
                        schoolInfo.setRemainDays(null); // 永久有效
                    } else {
                        LocalDateTime now = LocalDateTime.now();
                        LocalDateTime expireTime = school.getExpireTime();
                        if (now.isAfter(expireTime)) {
                            schoolInfo.setRemainDays(0); // 已过期
                        } else {
                            // 计算剩余天数
                            long remainDays = ChronoUnit.DAYS.between(now, expireTime);
                            schoolInfo.setRemainDays((int) remainDays);
                        }
                    }

                }
                schoolAndUserInfoList.add(schoolInfo);
            }
            resModel.setSchoolInfoList(schoolAndUserInfoList);
        }
        return resModel;
    }

    /**
     * 获取学校所有权限
     */
    private List<Long> getSchoolAllPermissions(Long schoolId) {
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
                        .in(MenuEntity::getId, menuIds));

        if (menus == null) {
            return new ArrayList<>();
        }

        return menus.stream()
                .filter(Objects::nonNull)  // 同时检查menu对象和permission
                .map(MenuEntity::getId)
                .distinct()
                .collect(Collectors.toList());
    }

}