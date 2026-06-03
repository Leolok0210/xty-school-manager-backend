package com.xiaotiyun.school.manager.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.constant.NumberConstant;
import com.xiaotiyun.school.manager.basic.enums.*;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.LanguageUtils;
import com.xiaotiyun.school.manager.dao.UserDao;
import com.xiaotiyun.school.manager.dao.UserGroupDao;
import com.xiaotiyun.school.manager.dao.UserSchoolRelDao;
import com.xiaotiyun.school.manager.handler.ExportFileHandler;
import com.xiaotiyun.school.manager.listener.UserImportEnUsListener;
import com.xiaotiyun.school.manager.listener.UserImportPtPtListener;
import com.xiaotiyun.school.manager.listener.UserImportZhTwListener;
import com.xiaotiyun.school.manager.model.dto.ImportRecordSaveDTO;
import com.xiaotiyun.school.manager.model.dto.UserImportDTO;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.excel.*;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.*;
import com.xiaotiyun.school.manager.util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {

    @Resource
    private UserSchoolRelDao userSchoolRelDao;
    @Resource
    private UserGroupDao userGroupDao;
    @Resource
    LanguageUtil languageUtil;
    @Resource
    private ImportTaskService importTaskService;
    @Resource
    private ImportRecordService importRecordService;
    @Resource
    private ExportFileHandler exportFileHandler;
    @Resource
    private UserSchoolRelService userSchoolRelService;
    @Resource
    private UserDeptRelService userDeptRelService;
    @Resource
    private UserClassRelService userClassRelService;
    @Resource
    private DeptService deptService;
    @Resource
    private UserSettingService userSettingService;
    private static final ExecutorService userImportPool = new ThreadPoolExecutor(10, 15, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100));
    // 定义格式
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addUser(UserAddReqModel reqModel, long schoolId) {
        // 1. 检查用户是否已经存在
        List<UserEntity> oldUsers = baseMapper.selectList(
                new LambdaQueryWrapper<UserEntity>()
                        .eq(UserEntity::getLoginName, reqModel.getLoginName())
                        .or()
                        .eq(UserEntity::getMobile, reqModel.getMobile())
        );

        // 2. 如果用户不存在，创建新用户
        UserEntity user = null;
        if (ObjectUtils.isEmpty(oldUsers)) {
            user = new UserEntity();
            user.setLoginName(reqModel.getLoginName());
            user.setMobileHead(reqModel.getMobileHead());
            user.setMobile(reqModel.getMobile());
            // 使用新的密码加密方式
            user.setPassword(PasswordUtil.encryptPassword("123456"));
            user.setNeedResetPwd(NumberConstant.ONE);
            user.setGender(reqModel.getGender());
            user.setUsername(reqModel.getUsername());
            save(user);
        } else {
            // 用户存在时
            for (UserEntity oldUser : oldUsers) {
                // 当属于同一个学校时
                if (userSchoolRelDao.selectCount(Wrappers.<UserSchoolRelEntity>lambdaQuery()
                        .eq(UserSchoolRelEntity::getUserId, oldUser.getId())
                        .eq(UserSchoolRelEntity::getSchoolId, schoolId)) > 0) {
                    if (reqModel.getLoginName().equals(oldUser.getLoginName()))
                        throw new BusinessException(LanguageConstants.USERNAME_EXISTS);
                    if (StringUtils.isNotBlank(reqModel.getMobile()) &&
                            StringUtils.isNotBlank(oldUser.getMobile()) &&
                            oldUser.getMobile().equals(reqModel.getMobile()))
                        throw new BusinessException(LanguageConstants.MOBILE_EXISTS);
                } else {
                    // 当不属于同一个学校时，用户名相等手机号不相等时报错
                    if (reqModel.getLoginName().equals(oldUser.getLoginName()) &&
                            StringUtils.isNotBlank(reqModel.getMobile()) &&
                            StringUtils.isNotBlank(oldUser.getMobile()) &&
                            !oldUser.getMobile().equals(reqModel.getMobile())) {
                        throw new BusinessMessageException(String.format(languageUtil.getMessage(LanguageConstants.MOBILE_NOT_MATCH), oldUser.getMobile()));
                    }
                    // 用户名不相等手机号相等时报错
                    if (!reqModel.getLoginName().equals(oldUser.getLoginName()) &&
                            StringUtils.isNotBlank(reqModel.getMobile()) &&
                            StringUtils.isNotBlank(oldUser.getMobile()) &&
                            oldUser.getMobile().equals(reqModel.getMobile())) {
                        throw new BusinessMessageException(String.format(languageUtil.getMessage(LanguageConstants.USERNAME_NOT_MATCH), oldUser.getLoginName()));
                    }
                }
                // 当原用户或新用户任一号码为空时，补充号码信息
                oldUser.setMobile(StringUtils.isNotBlank(reqModel.getMobile()) ? reqModel.getMobile() : oldUser.getMobile());
                oldUser.setMobileHead(reqModel.getMobileHead());
                updateById(oldUser);
                user = oldUser;
            }
        }

        // 3. 检查用户是否已经关联该学校
        UserSchoolRelEntity existingRel = userSchoolRelDao.selectOne(
                new LambdaQueryWrapper<UserSchoolRelEntity>()
                        .eq(UserSchoolRelEntity::getUserId, user.getId())
                        .eq(UserSchoolRelEntity::getSchoolId, schoolId)
                        .eq(UserSchoolRelEntity::getDeleted, false)
        );

        if (existingRel != null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.USER_ALREADY_BOUND_SCHOOL));
        }

        // 4. 检查用户编号是否重复
        UserSchoolRelEntity userSchoolRel = userSchoolRelDao.selectOne(
                new LambdaQueryWrapper<UserSchoolRelEntity>()
                        .eq(UserSchoolRelEntity::getUserNumber, reqModel.getUserNumber())
                        .eq(UserSchoolRelEntity::getSchoolId, schoolId)
        );

        if (userSchoolRel != null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.USER_NUMBER_REPEAT));
        }

        // 5. 新增用户-学校关联信息
        UserSchoolRelEntity userSchool = new UserSchoolRelEntity();
        userSchool.setUserId(user.getId());
        userSchool.setSchoolId(schoolId);
        userSchool.setUsername(reqModel.getUsername());
        userSchool.setUserType(reqModel.getUserType());
//        userSchool.setPosition(reqModel.getPosition());
        userSchool.setGender(reqModel.getGender());
//        userSchool.setStatus(reqModel.getStatus());
        userSchool.setUserGroupIds(String.join(",", reqModel.getUserGroupIds().stream()
                .map(String::valueOf).collect(Collectors.toList())));
        userSchool.setUserNumber(reqModel.getUserNumber());
//        generateUserNumber(schoolId, userSchool);
        userSchoolRelDao.insert(userSchool);
        // 处理部门
        userDeptRelService.saveUserDept(reqModel, schoolId, userSchool);
        // 处理班级权限
        userClassRelService.saveUserClass(reqModel, schoolId, userSchool);
    }

    private void generateUserNumber(Long schoolId, UserSchoolRelEntity userSchool) {
        //用戶編號生成規則：
        //字母 t + 学校ID+创建日期（yyMMdd ）+时间点（HHmmss）+ 4位数序号（0001 开始，当天学校下添加的第几个用户，每日重置）
        StringBuilder userNumber = new StringBuilder();
        userNumber.append("t").append(schoolId);
        //查询本校今日创建的用户数
        QueryWrapper<UserSchoolRelEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UserSchoolRelEntity::getSchoolId, schoolId)
                .eq(UserSchoolRelEntity::getDeleted, 0);
        if (userSchool.getCreateTime() != null) {
            //有创建时间的按创建时的时间
            userNumber.append(userSchool.getCreateTime().format(formatter));
            wrapper.lambda().isNotNull(UserSchoolRelEntity::getUserNumber)
                    .ge(UserSchoolRelEntity::getCreateTime, userSchool.getCreateTime().toLocalDate().atStartOfDay())
                    .le(UserSchoolRelEntity::getCreateTime, userSchool.getCreateTime().toLocalDate().atTime(LocalTime.MAX).withNano(0));
        } else {
            userNumber.append(LocalDateTime.now().format(formatter));
            wrapper.lambda().ge(UserSchoolRelEntity::getCreateTime, LocalDate.now().atStartOfDay())
                    .le(UserSchoolRelEntity::getCreateTime, LocalDate.now().atTime(LocalTime.MAX).withNano(0));
        }
        Long count = userSchoolRelDao.selectCount(wrapper);
        if (count != null) {
            userNumber.append(String.format("%04d", count + 1));
        } else {
            userNumber.append(String.format("%04d", 1));
        }
        userSchool.setUserNumber(userNumber.toString());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserUpdateReqModel reqModel, long schoolId) {
        // 1. 校验用户是否存在
        UserEntity user = getById(reqModel.getId());
        if (user == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.USER_NOT_EXISTS));
        }

        // 校验手机号是否重复
        if (reqModel.getMobile() != null) {
            UserEntity oldUser = getOne(Wrappers.<UserEntity>lambdaQuery().eq(UserEntity::getMobile, reqModel.getMobile()));
            if (oldUser != null && !Objects.equals(oldUser.getId(), reqModel.getId())) {
                throw new BusinessMessageException(String.format(languageUtil.getMessage(LanguageConstants.USERNAME_NOT_MATCH), oldUser.getLoginName()));
            }
            // 检查是否需要修改
            if (!reqModel.getMobile().equals(user.getMobile()) ||
                    !reqModel.getMobileHead().equals(user.getMobileHead()) ||
                    !reqModel.getUsername().equals(user.getUsername()) ||
                    !reqModel.getGender().equals(user.getGender())) {
                user.setMobile(reqModel.getMobile());
                user.setMobileHead(reqModel.getMobileHead());
                user.setUsername(reqModel.getUsername());
                user.setGender(reqModel.getGender());
                updateById(user);
            }
        }


        // 2. 更新用户-学校关联信息
        UserSchoolRelEntity userSchool = userSchoolRelDao.selectOne(
                new LambdaQueryWrapper<UserSchoolRelEntity>()
                        .eq(UserSchoolRelEntity::getUserId, reqModel.getId())
                        .eq(UserSchoolRelEntity::getSchoolId, schoolId)
                        .eq(UserSchoolRelEntity::getDeleted, false)
        );
        if (userSchool == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.USER_NOT_BOUND_SCHOOL));
        }

        // 3. 检查用户编号是否重复
        UserSchoolRelEntity userSchoolRel = userSchoolRelDao.selectOne(
                new LambdaQueryWrapper<UserSchoolRelEntity>()
                        .eq(UserSchoolRelEntity::getUserNumber, reqModel.getUserNumber())
                        .eq(UserSchoolRelEntity::getSchoolId, schoolId)
        );

        if (userSchoolRel != null && !userSchoolRel.getUserId().equals(reqModel.getId())) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.USER_NUMBER_REPEAT));
        }

        userSchool.setUsername(reqModel.getUsername());
        userSchool.setUserType(reqModel.getUserType());
//        userSchool.setPosition(reqModel.getPosition());
        userSchool.setGender(reqModel.getGender());
//        userSchool.setStatus(reqModel.getStatus());
        userSchool.setUserGroupIds(String.join(",", reqModel.getUserGroupIds().stream()
                .map(String::valueOf).collect(Collectors.toList())));
        userSchool.setUserNumber(reqModel.getUserNumber());
//        if (!StringUtils.isNotBlank(userSchool.getUserNumber())) {
//            //无用户编号的重新生成用户编号
//            generateUserNumber(schoolId, userSchool);
//        }
        userSchoolRelDao.updateById(userSchool);
        // 更新部门
        userDeptRelService.updateUserDept(reqModel, schoolId, userSchool);
        // 更新班级权限
        userClassRelService.updateUserClass(reqModel, schoolId, userSchool);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> deleteUser(UserDeleteReqModel reqModel) {
        // 1. 校验用户是否存在
        UserEntity user = getById(reqModel.getId());
        if (user == null) {
            return Result.failed(ResultCode.FAILED.getCode(),languageUtil.getMessage(LanguageConstants.USER_NOT_EXISTS));
        }
        // 2. 查询当前用户的所有未删除的学校关联
        List<UserSchoolRelEntity> userSchoolList = userSchoolRelDao.selectList(
                new LambdaQueryWrapper<UserSchoolRelEntity>()
                        .eq(UserSchoolRelEntity::getUserId, reqModel.getId())
                        .eq(UserSchoolRelEntity::getDeleted, false)
        );

        // 3. 获取指定的用户-学校关联
        UserSchoolRelEntity userSchool = userSchoolList.stream()
                .filter(rel -> rel.getSchoolId().equals(reqModel.getSchoolId()))
                .findFirst()
                .orElse(null);

        // 4. 开始删除
        if (userSchool != null) {
            // 是否预删除,如果是预删除，只检查用户是否绑定多个部门
            if (reqModel.getIsPre() == 1){
                if (reqModel.getDeptId() == 0L) {
                    return Result.success(4);// 删除用户时，选择的部门层级是“学校”，返回是否全部删除
                }
                List<UserDeptRelEntity> deptList = userDeptRelService.list(Wrappers.<UserDeptRelEntity>lambdaQuery()
                        .eq(UserDeptRelEntity::getSchoolId, reqModel.getSchoolId())
                        .eq(UserDeptRelEntity::getUserId, userSchool.getId()));
                if (deptList.isEmpty()) {
                    return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.DEPARTMENT_NOT_EXISTS));
                }
                if (deptList.size() == 1) {
                    return Result.success(1);// 只有一个部门时，直接删除
                }
                if (ObjectUtils.isNotEmpty(deptList.stream()
                        .filter(rel -> rel.getDeptId().equals(reqModel.getDeptId()) && rel.getIsMaster() == 1)
                        .findFirst()
                        .orElse(null))) {
                    return Result.success(2);// 当删除的是主部门时，返回是否全部删除
                }
                return Result.success(3);// 当删除的是子部门时，返回可选的是否全部删除
            } else if(reqModel.getIsPre() == 0){
                if (reqModel.getIsAll() == 0){
                    // 不全删时，只删除和当前部门的关联
                    userDeptRelService.remove(Wrappers.<UserDeptRelEntity>lambdaQuery()
                            .eq(UserDeptRelEntity::getSchoolId, reqModel.getSchoolId())
                            .eq(UserDeptRelEntity::getUserId, userSchool.getId())
                            .eq(UserDeptRelEntity::getDeptId, reqModel.getDeptId()));
                } else if (reqModel.getIsAll() == 1) {
                    // 删除和学校的关联
                    userSchoolRelDao.deleteById(userSchool.getId());
                    // 删除部门关联和班级权限
                    userDeptRelService.remove(Wrappers.<UserDeptRelEntity>lambdaQuery()
                            .eq(UserDeptRelEntity::getSchoolId, reqModel.getSchoolId())
                            .eq(UserDeptRelEntity::getUserId, userSchool.getId()));
                    userClassRelService.remove(Wrappers.<UserClassRelEntity>lambdaQuery()
                            .eq(UserClassRelEntity::getSchoolId, reqModel.getSchoolId())
                            .eq(UserClassRelEntity::getUserId, userSchool.getId()));
                    // 如果只有一个学校关联，删除用户基本信息
                    boolean shouldDeleteUser = userSchoolList.size() == 1 &&
                            userSchoolList.get(0).getSchoolId().equals(reqModel.getSchoolId());
                    // 如果没有其他学校关联了，则删除用户基本信息
                    if (shouldDeleteUser) {
                        removeById(reqModel.getId());
                    }
                }
            }
        }
        return Result.success();
    }

    @Override
    public UserDetailResModel getUserDetail(Long id, Long schoolId) {
        // 1. 获取用户基本信息
        UserEntity user = getById(id);
        if (user == null) {
            throw new BusinessException(LanguageConstants.USER_NOT_EXISTS);
        }

        // 2. 获取用户-学校关联信息
        UserSchoolRelEntity userSchool = userSchoolRelDao.selectOne(
                new LambdaQueryWrapper<UserSchoolRelEntity>()
                        .eq(UserSchoolRelEntity::getUserId, id)
                        .eq(UserSchoolRelEntity::getSchoolId, schoolId)
                        .eq(UserSchoolRelEntity::getDeleted, false)
        );
        if (userSchool == null) {
            throw new BusinessException(LanguageConstants.USER_NOT_BOUND_SCHOOL);
        }

        // 3. 组装返回数据
        UserDetailResModel resModel = new UserDetailResModel();
        resModel.setId(user.getId());
        resModel.setMobileHead(user.getMobileHead());
        resModel.setMobile(user.getMobile());
        resModel.setUserNumber(userSchool.getUserNumber());
        resModel.setUserType(user.getUserType());
        resModel.setUsername(userSchool.getUsername());
        resModel.setLoginName(user.getLoginName());
        resModel.setSchoolUserType(userSchool.getUserType());
//        resModel.setPosition(userSchool.getPosition());
        resModel.setGender(userSchool.getGender());
//        resModel.setStatus(userSchool.getStatus());
        resModel.setLoginName(user.getLoginName());
        resModel.setNeedResetPwd(user.getNeedResetPwd());
        resModel.setUserGroupIds(userSchool.getUserGroupIds());

        // 组装用户组信息，如果是超级管理员，则赋值为超级管理员
        if (UserTypeEnum.isSuperAdmin(user.getUserType())) {
            resModel.setUserGroupName(UserTypeEnum.SUPER_ADMIN.getDesc());
        } else {
            // 如果userSchool.getUserGroupIds()存在，则获取第一个id去查询用户组名称
            if (StringUtils.isNotBlank(userSchool.getUserGroupIds())) {
                String[] userGroupIds = userSchool.getUserGroupIds().split(",");
                if (userGroupIds.length > 0) {
                    List<Long> groupId = Arrays.stream(userGroupIds).map(Long::parseLong).collect(Collectors.toList());
                    List<UserGroupEntity> userGroup = userGroupDao.selectBatchIds(groupId);
                    // 判断是否学校管理员
                    if (ObjectUtils.isNotEmpty(userGroup)) {
                        if (userGroup.stream().anyMatch(entity -> UserGroupTypeEnum.isSchoolAdmin(entity.getCode()))) {
                            resModel.setUserType(3);
                            resModel.setUserGroupName(UserTypeEnum.SCHOOL_ADMIN.getDesc());
                        } else {
                            resModel.setUserGroupName(userGroup.get(0).getName());
                        }
                    }
                }
            }
        }
        // 组装部门信息
        getUserDetailDeptInfo(schoolId,userSchool,resModel);
        // 拼装班级权限
        getUserDetailClassInfo(schoolId,userSchool,resModel);
        // 查询用户设定
        getUserDetailSettingInfo(userSchool,resModel);
        return resModel;
    }


    @Override
    public Integer getUserRole(Long id, Long schoolId) {
        // 1. 获取用户基本信息
        UserEntity user = getById(id);
        if (user == null) {
            throw new BusinessException(LanguageConstants.USER_NOT_EXISTS);
        }
        // 组装用户组信息，如果是超级管理员，则赋值为超级管理员
        if (UserTypeEnum.isSuperAdmin(user.getUserType())) {
            return UserTypeEnum.SUPER_ADMIN.getCode();
        } else {
            // 2. 获取用户-学校关联信息
            UserSchoolRelEntity userSchool = userSchoolRelDao.selectOne(
                    new LambdaQueryWrapper<UserSchoolRelEntity>()
                            .eq(UserSchoolRelEntity::getUserId, id)
                            .eq(UserSchoolRelEntity::getSchoolId, schoolId)
                            .eq(UserSchoolRelEntity::getDeleted, false)
            );
            if (userSchool == null) {
                throw new BusinessException(LanguageConstants.USER_NOT_BOUND_SCHOOL);
            }
            // 如果userSchool.getUserGroupIds()存在，则获取第一个id去查询用户组名称
            if (StringUtils.isNotBlank(userSchool.getUserGroupIds())) {
                String[] userGroupIds = userSchool.getUserGroupIds().split(",");
                if (userGroupIds.length > 0) {
                    List<Long> groupId = Arrays.stream(userGroupIds).map(Long::parseLong).collect(Collectors.toList());
                    List<UserGroupEntity> userGroup = userGroupDao.selectBatchIds(groupId);
                    // 判断是否学校管理员
                    if (ObjectUtils.isNotEmpty(userGroup)) {
                        if (userGroup.stream().anyMatch(entity -> UserGroupTypeEnum.isSchoolAdmin(entity.getCode()))) {
                            return UserTypeEnum.SCHOOL_ADMIN.getCode();
                        }
                    }
                }
            }
        }
        return UserTypeEnum.NORMAL_USER.getCode();
    }

    private void getUserDetailSettingInfo(UserSchoolRelEntity userSchool, UserDetailResModel resModel) {
        // 查询用户设定
        List<UserSettingEntity> userSetting = userSettingService.list(Wrappers.<UserSettingEntity>lambdaQuery()
                .eq(UserSettingEntity::getUserId, userSchool.getUserId()));
        if (ObjectUtils.isNotEmpty(userSetting)) {
            resModel.setUserSettings(userSetting.stream().map(userSettingEntity -> {
                UserSettingResModel userSettingResModel = new UserSettingResModel();
                BeanUtils.copyProperties(userSettingEntity, userSettingResModel);
                return userSettingResModel;
            }).collect(Collectors.toList()));
        }
    }

    private void getUserDetailClassInfo(Long schoolId, UserSchoolRelEntity userSchool, UserDetailResModel resModel) {
        // 拼装班级权限
        List<UserClassRelEntity> userClassRelList = userClassRelService.list(Wrappers.<UserClassRelEntity>lambdaQuery()
               .eq(UserClassRelEntity::getSchoolId, schoolId)
               .eq(UserClassRelEntity::getUserId, userSchool.getId()));
        if (ObjectUtils.isNotEmpty(userClassRelList)) {
            resModel.setRelList(userClassRelList.stream().map(userClassRelEntity -> {
                UserClassRelResModel userClassRelResModel = new UserClassRelResModel();
                BeanUtils.copyProperties(userClassRelEntity, userClassRelResModel);
                return userClassRelResModel;
            }).collect(Collectors.toList()));
        }
    }

    private void getUserDetailDeptInfo(Long schoolId, UserSchoolRelEntity userSchool, UserDetailResModel resModel) {
        // 组装部门信息
        List<UserDeptRelEntity> deptList = userDeptRelService.list(Wrappers.<UserDeptRelEntity>lambdaQuery()
                .eq(UserDeptRelEntity::getSchoolId, schoolId)
                .eq(UserDeptRelEntity::getUserId, userSchool.getId()));
        if (ObjectUtils.isNotEmpty(deptList)) {
            // 部门信息
            List<Long> deptId = deptList.stream().map(UserDeptRelEntity::getDeptId).collect(Collectors.toList());
            List<DeptEntity> deptEntities = deptService.listByIds(deptId);
            if (ObjectUtils.isNotEmpty(deptEntities)) {
                Map<Long, DeptEntity> deptMap = deptEntities.stream()
                        .collect(Collectors.toMap(DeptEntity::getId, Function.identity()));
                // 主部门
                UserDeptDetailResModel masterDept = deptList.stream()
                        .filter(a -> a.getIsMaster() == 1)
                        .findFirst()
                        .map(a-> {
                            UserDeptDetailResModel res = new UserDeptDetailResModel();
                            res.setId(a.getDeptId());
                            res.setIsLeader(a.getIsAdmin());
                            res.setName(deptMap.get(a.getDeptId()) == null ? "":deptMap.get(a.getDeptId()).getName());
                            return res;
                        })
                        .orElse(null);
                resModel.setMasterDept(masterDept);
                // 兼职部门
                List<UserDeptDetailResModel> slaveDeptList = deptList.stream()
                        .filter(a -> a.getIsMaster() == 0)
                        .map(a-> {
                            UserDeptDetailResModel res = new UserDeptDetailResModel();
                            res.setId(a.getDeptId());
                            res.setIsLeader(a.getIsAdmin());
                            res.setName(deptMap.get(a.getDeptId()) == null? "":deptMap.get(a.getDeptId()).getName());
                            return res;
                        }).collect(Collectors.toList());
                resModel.setSlaveDeptList(slaveDeptList);
            }
        }
    }

    @Override
    public UserDetailResModel getUserDetail(Long id) {
        // 1. 获取用户基本信息
        UserEntity user = getById(id);
        if (user == null) {
            throw new BusinessException(LanguageConstants.USER_NOT_EXISTS);
        }

        // 3. 组装返回数据
        UserDetailResModel resModel = new UserDetailResModel();
        resModel.setId(user.getId());
        resModel.setMobileHead(user.getMobileHead());
        resModel.setMobile(user.getMobile());
        resModel.setUsername(user.getUsername());
        resModel.setGender(user.getGender());
        resModel.setLoginName(user.getLoginName());
        resModel.setUserType(user.getUserType());
        if (UserTypeEnum.isSuperAdmin(user.getUserType())) {
            resModel.setUserGroupName(UserTypeEnum.SUPER_ADMIN.getDesc());
        }
        return resModel;
    }

    @Override
    public PageInfo<UserDetailResModel> getUserPage(UserQueryReqModel reqModel, Long schoolId) {
        // 分页查询用户信息
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());

        List<UserDetailResModel> userList = getUserList(reqModel, schoolId);
        PageInfo<UserDetailResModel> pageInfo = new PageInfo<>(userList);
        return pageInfo;
    }

    private List<UserDetailResModel> getUserList(UserQueryReqModel reqModel, Long schoolId) {
        List<UserDetailResModel> userList = this.baseMapper.queryTeacherList(reqModel, schoolId);
        // 组装返回数据
        if (!userList.isEmpty()){
            //获取用户组信息
            List<Long> userGroupIds = userList.stream()
                    .flatMap(userSchool -> Arrays.stream(userSchool.getUserGroupIds().split(",")))
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
            List<UserGroupEntity> userGroups = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(userGroupIds)) {
                userGroups = userGroupDao.selectBatchIds(userGroupIds);
            }
            Map<Long, UserGroupEntity> userGroupMap = userGroups.stream()
                    .collect(Collectors.toMap(UserGroupEntity::getId, userGroup -> userGroup));
            for (UserDetailResModel resModel : userList) {
                if (StringUtils.isNotBlank(resModel.getUserGroupIds())) {
                    List<Long> groupIds = Arrays.stream(resModel.getUserGroupIds().split(","))
                            .map(Long::valueOf)
                            .collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(groupIds)) {
                        List<String> groupNames = new ArrayList<>();
                        for (Long groupId : groupIds) {
                            UserGroupEntity userGroupEntity = userGroupMap.get(groupId);
                            if (userGroupEntity != null) {
                                groupNames.add(userGroupEntity.getName());
                            }
                        }
                        resModel.setUserGroupName(String.join(",", groupNames));
                    }
                }
            }
        }
        return userList;
    }

    @Override
    public void resetPassword(Long id) {
        // 使用新的密码加密方式重置密码
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setPassword(PasswordUtil.encryptPassword("123456"));
        user.setNeedResetPwd(NumberConstant.ONE);
        updateById(user);
    }

    /**
     * 检查手机号是否已存在
     *
     * @param mobile 手机号
     * @return true-存在，false-不存在
     */
    @Override
    public boolean isMobileExists(String mobile) {
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getMobile, mobile);
        return baseMapper.exists(wrapper);
    }


    @Override
    public UserMobileCheckResModel checkMobileExists(String mobile, Long schoolId) {
        UserMobileCheckResModel result = new UserMobileCheckResModel();

        // 1. 查询用户基本信息
        UserEntity user = baseMapper.selectOne(
                new LambdaQueryWrapper<UserEntity>()
                        .eq(UserEntity::getMobile, mobile)
                        .eq(UserEntity::getDeleted, false)
        );

        result.setExists(user != null);

        if (user != null) {
            // 查询用户是否绑定了当前学校
            UserSchoolRelEntity userSchool = userSchoolRelDao.selectOne(
                    new LambdaQueryWrapper<UserSchoolRelEntity>()
                            .eq(UserSchoolRelEntity::getUserId, user.getId())
                            .eq(UserSchoolRelEntity::getSchoolId, schoolId)
                            .eq(UserSchoolRelEntity::getDeleted, false)
            );
            result.setBoundCurrentSchool(userSchool != null);
        } else {
            result.setBoundCurrentSchool(false);
        }

        return result;
    }

    @Override
    public List<TeacherListResModel> getTeachersBySchool(Long schoolId) {
        return this.baseMapper.selectTeachersBySchool(schoolId);
    }

    @Override
    public Long importUser(Long schoolId, MultipartFile file) {
        if (schoolId == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.NO_SCHOOL_FILE_CONTENT_EMPTY));
        }
        // 获取学校语言设置信息
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        if (StringUtils.isEmpty(currentLanguage)) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        SchoolLanguageEnum languageEnum = SchoolLanguageEnum.getDefValue(currentLanguage);
        if (languageEnum == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        // 读取Excel文件
        List<UserImportModel> list = readExcelData(file, languageEnum);
        if (CollectionUtils.isNotEmpty(list)) {
            // 创建导入任务
            ImportTaskEntity task = new ImportTaskEntity();
            task.setSchoolId(schoolId);
            task.setFileName(file.getOriginalFilename());
            task.setType(ImportTaskTypeEnum.USER_INFO.getCode());
            task.setTotalCount(0);
            task.setSuccessCount(0);
            task.setFailCount(0);
            importTaskService.save(task);
            CompletableFuture.runAsync(() -> {
                languageUtil.setLanguage(languageEnum.getCode());
                handleUserImport(task, list, schoolId, languageEnum);
                LanguageUtil.clearLanguage();
            }, userImportPool).whenComplete((res, ex) -> {
                if (ex != null) {
                    log.error("导入用户任务执行结束taskId=【{}】异常={}",task.getId(),ex);
                } else {
                    log.info("导入用户完成，任务ID={}",task.getId());
                }
                task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
                importTaskService.updateById(task);
            });

            return task.getId();
        }
        return null;
    }

    private void handleUserImport(ImportTaskEntity task, List<UserImportModel> list, Long schoolId, SchoolLanguageEnum schoolLanguageEnum) {
        if (CollectionUtils.isNotEmpty(list)) {
            task.setTotalCount(list.size());
            task.setStatus(ImportTaskStatusEnum.IN_PROCESS.getCode());
            importTaskService.updateById(task);
            log.info("开始处理数据导入...");
            Iterator<UserImportModel> iterator = list.iterator();
            //每500个处理一次
            List<UserImportModel> batchExcelLine = new ArrayList<>(500);
            Map<String, List<UserImportModel>> uNameMap = new HashMap<>();
            Map<String, List<UserImportModel>> phoneMap = new HashMap<>();
            Map<String, List<UserImportModel>> userNumberMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(list)) {
                uNameMap = list.stream().filter(userImportModel -> StringUtils.isNotBlank(userImportModel.getUName())).collect(Collectors.groupingBy(UserImportModel::getUName));
                phoneMap = list.stream().filter(userImportModel -> StringUtils.isNotBlank(userImportModel.getPhone()) && StringUtils.isNotBlank(userImportModel.getPhoneArea())).collect(Collectors.groupingBy(userImportModel -> userImportModel.getPhoneArea() + "_" + userImportModel.getPhone()));
                userNumberMap = list.stream().filter(userImportModel -> StringUtils.isNotBlank(userImportModel.getUserNumber())).collect(Collectors.groupingBy(UserImportModel::getUserNumber));
            }
            //获取用户信息
            List<String> uNames = list.stream().map(UserImportModel::getUName).collect(Collectors.toList());
            Map<String, UserEntity> userLoginNameMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(uNames)) {
                QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().in(UserEntity::getLoginName, uNames);
                List<UserEntity> userEntities = this.list(queryWrapper);
                if (CollectionUtils.isNotEmpty(userEntities)) {
                    userLoginNameMap = userEntities.stream().collect(Collectors.toMap(UserEntity::getLoginName, user -> user));
                }
            }
            List<String> phones = list.stream().map(UserImportModel::getPhone).collect(Collectors.toList());
            Map<String, UserEntity> userPhoneMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(phones)) {
                QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().in(UserEntity::getMobile, phones);
                List<UserEntity> userEntities = this.list(queryWrapper);
                if (CollectionUtils.isNotEmpty(userEntities)) {
                    userPhoneMap = userEntities.stream().collect(Collectors.toMap(UserEntity::getMobile, user -> user));
                }
            }
            //获取学校用户信息
            List<String> userNumbers = list.stream().map(UserImportModel::getUserNumber).collect(Collectors.toList());
            Map<String, UserSchoolRelEntity> schoolUserNumberMap = new HashMap<>();
            Map<Long, UserEntity> userMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(userNumbers)) {
                QueryWrapper<UserSchoolRelEntity> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(UserSchoolRelEntity::getSchoolId, schoolId)
                        .in(UserSchoolRelEntity::getUserNumber, userNumbers);
                List<UserSchoolRelEntity> userSchoolRelEntities = userSchoolRelDao.selectList(wrapper);
                if (CollectionUtils.isNotEmpty(userSchoolRelEntities)) {
                    schoolUserNumberMap = userSchoolRelEntities.stream().collect(Collectors.toMap(UserSchoolRelEntity::getUserNumber, userSchoolRelEntity -> userSchoolRelEntity));
                    List<Long> userIds = userSchoolRelEntities.stream().map(UserSchoolRelEntity::getUserId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(userIds)) {
                        QueryWrapper<UserEntity> userWrapper = new QueryWrapper<>();
                        userWrapper.lambda().in(UserEntity::getId, userIds);
                        List<UserEntity> userEntities = this.list(userWrapper);
                        if (CollectionUtils.isNotEmpty(userEntities)) {
                            userMap = userEntities.stream().collect(Collectors.toMap(UserEntity::getId, user -> user));
                        }
                    }
                }
            }
            //获取学校用户组信息
            QueryWrapper<UserGroupEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(UserGroupEntity::getSchoolId, schoolId)
                    .or()
                    .eq(UserGroupEntity::getSchoolId, 0);
            List<UserGroupEntity> userGroupList = userGroupDao.selectList(queryWrapper);
            // 获取部门信息
            List<DeptEntity> deptList = deptService.list(Wrappers.<DeptEntity>lambdaQuery().eq(DeptEntity::getSchoolId, schoolId));
            Map<String, DeptEntity> deptMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(deptList)) {
                deptMap = deptList.stream().collect(Collectors.toMap(DeptEntity::getName, dept -> dept));
            }
            int correctCount = 0;
            List<ImportRecordSaveDTO> importRecordSaveDTOS = new ArrayList<>();
            while (iterator.hasNext()) {
                UserImportModel userImportModel = iterator.next();
                batchExcelLine.add(userImportModel);
                if (batchExcelLine.size() >= 500) {
                    //处理数据 插入数据库
                    correctCount += processBatchExcelLine(importRecordSaveDTOS, batchExcelLine, schoolId, uNameMap, phoneMap, userNumberMap,
                            schoolLanguageEnum, userLoginNameMap, userPhoneMap, schoolUserNumberMap, userGroupList, userMap, deptMap);
                    batchExcelLine.clear();
                }
            }
            if (!batchExcelLine.isEmpty()) {
                correctCount += processBatchExcelLine(importRecordSaveDTOS, batchExcelLine, schoolId, uNameMap, phoneMap, userNumberMap,
                        schoolLanguageEnum, userLoginNameMap, userPhoneMap, schoolUserNumberMap, userGroupList, userMap, deptMap);
                batchExcelLine.clear();
            }
            //当前处理进度写入数据库
            task.setSuccessCount(correctCount);
            task.setFailCount(list.size() - correctCount);
            task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
            importTaskService.updateById(task);
            //错误信息写入数据库
            if (CollectionUtils.isNotEmpty(importRecordSaveDTOS)) {
                List<ImportRecordEntity> entityList = importRecordSaveDTOS.stream().map(dto -> {
                    ImportRecordEntity importRecordEntity = new ImportRecordEntity();
                    BeanUtils.copyProperties(dto, importRecordEntity);
                    importRecordEntity.setTaskId(task.getId());
                    return importRecordEntity;
                }).collect(Collectors.toList());
                importRecordService.saveBatch(entityList);
            }
        } else {
            task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
            importTaskService.updateById(task);
        }
    }

    private Boolean validateUName(String uName) {
        String regex = "^[a-zA-Z0-9]{8,20}$";
        return uName.matches(regex);
    }

    private Boolean validatePhone(String phoneArea, String phone) {
        boolean result;
        if (phoneArea.equals("86")) {
            result = phone.length() == 11;
        } else {
            result = phone.length() == 8;
        }
        return result;
    }

    private boolean check(UserImportModel bo, List<String> errorList, Map<String, List<UserImportModel>> uNameMap,
                          Map<String, List<UserImportModel>> phoneMap, Map<String, List<UserImportModel>> userNumberMap,
                          Map<String, UserEntity> userLoginNameMap, Map<String, UserEntity> userPhoneMap,
                          Map<String, UserSchoolRelEntity> schoolUserNumberMap, List<UserGroupEntity> userGroupList,
                          Map<Long, UserEntity> userMap) {
        //一项一项检查
        if (!StringUtils.isNotBlank(bo.getUName())) {
            errorList.add(languageUtil.getMessage(LanguageConstants.LOGIN_NAME_REQUIRED));
        } else {
            if (!validateUName(bo.getUName())) {
                errorList.add(languageUtil.getMessage(LanguageConstants.LOGIN_NAME_FORMAT_ERROR));
            }
            if (CollectionUtils.isNotEmpty(uNameMap.get(bo.getUName())) && uNameMap.get(bo.getUName()).size() > 1) {
                errorList.add(languageUtil.getMessage(LanguageConstants.LOGIN_NAME_DUPLICATED_IN_EXCEL));
            }
            UserEntity userEntity = userLoginNameMap.get(bo.getUName());
            if (userEntity != null) {
                if (StringUtils.isNotBlank(userEntity.getMobile())) {
                    if (StringUtils.isNotBlank(bo.getPhone()) && !userEntity.getMobile().equals(bo.getPhone())) {
                        errorList.add(languageUtil.getMessage(LanguageConstants.USER_PHONE_BOUND_CURRENT_ERROR));
                    }
                } else {
                    if (StringUtils.isNotBlank(bo.getPhone()) && userPhoneMap.get(bo.getPhone()) != null && !userPhoneMap.get(bo.getPhone()).getLoginName().equals(userEntity.getLoginName())) {
                        errorList.add(languageUtil.getMessage(LanguageConstants.USER_PHONE_BOUND_CURRENT_ERROR));
                    }
                }
            } else {
                if (StringUtils.isNotBlank(bo.getPhone()) && userPhoneMap.get(bo.getPhone()) != null) {
                    errorList.add(languageUtil.getMessage(LanguageConstants.USER_PHONE_BOUND_ERROR));
                }
            }
        }
        if (StringUtils.isNotBlank(bo.getPhone())) {
            if (!StringUtils.isNotBlank(bo.getPhoneArea())) {
                errorList.add(languageUtil.getMessage(LanguageConstants.MOBILE_REGION_REQUIRED));
            }
            if (CollectionUtils.isNotEmpty(phoneMap.get(bo.getPhoneArea() + "_" + bo.getPhone())) && phoneMap.get(bo.getPhoneArea() + "_" + bo.getPhone()).size() > 1) {
                errorList.add(languageUtil.getMessage(LanguageConstants.MOBILE_DUPLICATED_IN_EXCEL));
            }
            if (!validatePhone(bo.getPhoneArea(), bo.getPhone())) {
                errorList.add(languageUtil.getMessage(LanguageConstants.MOBILE_LENGTH_INVALID));
            }
        }
        if (!StringUtils.isNotBlank(bo.getUserName())) {
            errorList.add(languageUtil.getMessage(LanguageConstants.USER_NAME_REQUIRED));
        } else {
            if (bo.getUserName().length() > 20) {
                errorList.add(languageUtil.getMessage(LanguageConstants.USER_NAME_LENGTH_INVALID));
            }
        }
        if (!StringUtils.isNotBlank(bo.getDeptName())) {
            errorList.add(languageUtil.getMessage(LanguageConstants.DEPARTMENT_NAME_NOT_EMPTY));
        }
        if (!StringUtils.isNotBlank(bo.getUserGroup())) {
            errorList.add(languageUtil.getMessage(LanguageConstants.USER_GROUP_REQUIRED));
        } else {
            if (CollectionUtils.isNotEmpty(userGroupList) && userGroupList.stream().noneMatch(userGroupEntity -> userGroupEntity.getName().equals(bo.getUserGroup()))) {
                errorList.add(languageUtil.getMessage(LanguageConstants.USER_GROUP_NOT_EXIST));
            }
        }
        if (StringUtils.isNotBlank(bo.getUserNumber())) {
            if (CollectionUtils.isNotEmpty(userNumberMap.get(bo.getUserNumber())) && userNumberMap.get(bo.getUserNumber()).size() > 1) {
                errorList.add(languageUtil.getMessage(LanguageConstants.USER_NUMBER_DUPLICATED_IN_EXCEL));
            }
            if (schoolUserNumberMap.get(bo.getUserNumber()) != null && userMap.get(schoolUserNumberMap.get(bo.getUserNumber()).getUserId()) != null && StringUtils.isNotBlank(bo.getUName())
                    && !userMap.get(schoolUserNumberMap.get(bo.getUserNumber()).getUserId()).getLoginName().equals(bo.getUName())) {
                errorList.add(languageUtil.getMessage(LanguageConstants.USER_NUMBER_EXISTS));
            }
        } else {
            errorList.add(languageUtil.getMessage(LanguageConstants.USER_NUMBER_REQUIRED));
        }
//        if (!StringUtils.isNotBlank(bo.getPosition())) {
//            errorList.add(languageUtil.getMessage(LanguageConstants.USER_POSITION_REQUIRED));
//        } else {
//            if (JobTitleEnum.getCode(bo.getPosition(), SchoolLanguageEnum.getDefValue(LanguageUtil.getCurrentLanguage())) == null) {
//                errorList.add(languageUtil.getMessage(LanguageConstants.USER_POSITION_FORMAT_ERROR));
//            }
//        }
        if (!StringUtils.isNotBlank(bo.getGender())) {
            errorList.add(languageUtil.getMessage(LanguageConstants.USER_GENDER_REQUIRED));
        } else {
            if (GenderEnum.getCode(bo.getGender(), SchoolLanguageEnum.getDefValue(LanguageUtil.getCurrentLanguage())) == null) {
                errorList.add(String.format(languageUtil.getMessage(LanguageConstants.GENDER_FORMAT_ERROR), bo.getGender()));
            }
        }
        return !CollectionUtils.isNotEmpty(errorList);
    }

    public int processBatchExcelLine(List<ImportRecordSaveDTO> importErrorDTOS, List<UserImportModel> list, Long schoolId,
                                     Map<String, List<UserImportModel>> uNameMap, Map<String, List<UserImportModel>> phoneMap,
                                     Map<String, List<UserImportModel>> userNumberMap, SchoolLanguageEnum schoolLanguageEnum,
                                     Map<String, UserEntity> userLoginNameMap, Map<String, UserEntity> userPhoneMap,
                                     Map<String, UserSchoolRelEntity> schoolUserNumberMap, List<UserGroupEntity> userGroupList,
                                     Map<Long, UserEntity> userMap, Map<String, DeptEntity> deptMap) {
        if (CollectionUtils.isNotEmpty(list)) {
            int correctCount = list.size();//正确处理的条数
            //待插入的学生信息
            List<UserImportDTO> saveOrUpdateList = new ArrayList<>();
            //遍历要插入的每一行
            for (UserImportModel bo : list) {
                List<String> errorList = new ArrayList<>();
                if (!check(bo, errorList, uNameMap, phoneMap, userNumberMap, userLoginNameMap, userPhoneMap, schoolUserNumberMap, userGroupList, userMap)) {
                    //不合法
                    correctCount--;
                    if (CollectionUtils.isNotEmpty(errorList)) {
                        ImportRecordSaveDTO errorDTO = new ImportRecordSaveDTO();
                        errorDTO.setIncorrectLineno(String.valueOf(bo.getExcelLineNo()));
                        errorDTO.setIncorrectReason(StringUtils.join(errorList, "；"));
                        importErrorDTOS.add(errorDTO);
                    }
                    continue;
                }
                UserImportDTO userImportDTO = userImportConvert(schoolId, bo, userLoginNameMap, schoolLanguageEnum);
                saveOrUpdateList.add(userImportDTO);
            }
            if (CollectionUtils.isNotEmpty(saveOrUpdateList)) {
                log.info("导入数据创建/更新用户信息开始");
                List<UserEntity> users = new ArrayList<>();
                saveOrUpdateList.forEach(userImportDTO -> {
                    UserEntity user = new UserEntity();
                    BeanUtils.copyProperties(userImportDTO, user);
                    users.add(user);
                });
                this.saveOrUpdateBatch(users);
                if (CollectionUtils.isNotEmpty(users)) {
                    Map<String, UserEntity> userEntityMap = users.stream().collect(Collectors.toMap(UserEntity::getLoginName, user -> user, (key1, key2) -> key1));
                    List<Long> userIds = users.stream().map(UserEntity::getId).collect(Collectors.toList());
                    Map<Long, UserSchoolRelEntity> schoolUserEntityMap = new HashMap<>();
                    if (CollectionUtils.isNotEmpty(userIds)) {
                        QueryWrapper<UserSchoolRelEntity> queryWrapper = new QueryWrapper<>();
                        queryWrapper.lambda().in(UserSchoolRelEntity::getUserId, userIds)
                                .eq(UserSchoolRelEntity::getSchoolId, schoolId);
                        List<UserSchoolRelEntity> schoolUsers = userSchoolRelDao.selectList(queryWrapper);
                        if (CollectionUtils.isNotEmpty(schoolUsers)) {
                            schoolUserEntityMap = schoolUsers.stream().collect(Collectors.toMap(UserSchoolRelEntity::getUserId, userSchoolRelEntity -> userSchoolRelEntity));
                        }
                    }

                    // 获取学校的用户主部门信息
                    List<UserDeptRelEntity> userDeptRelEntities = userDeptRelService.list(
                            Wrappers.<UserDeptRelEntity>lambdaQuery()
                                    .eq(UserDeptRelEntity::getSchoolId, schoolId).eq(UserDeptRelEntity::getIsMaster, 1)
                    );
                    // 转Map，key是用户ID，value是部门关系ID
                    Map<Long, Long> userDeptMap = userDeptRelEntities.stream().collect(Collectors.toMap(UserDeptRelEntity::getUserId, UserDeptRelEntity::getId));

                    Map<String, UserGroupEntity> userGroupMap = userGroupList.stream().collect(Collectors.toMap(UserGroupEntity::getName, userGroupEntity -> userGroupEntity));
                    List<UserSchoolRelEntity> schoolUserSaveOrUpdateList = new ArrayList<>();
                    Map<Long, UserImportDTO> userIdDtoMap = new HashMap<>();
                    for (UserImportDTO userImportDTO : saveOrUpdateList) {
                        // 处理与学校关联关系
                        UserEntity userEntity = userEntityMap.get(userImportDTO.getLoginName());
                        if (userEntity != null) {
                            UserSchoolRelEntity userSchoolRelEntity = new UserSchoolRelEntity();
                            UserSchoolRelEntity userSchoolRel = schoolUserEntityMap.get(userEntity.getId());
                            List<Long> userGroupIds = new ArrayList<>();
                            if (userSchoolRel != null) {
                                userSchoolRelEntity.setId(userSchoolRel.getId());
                                if (StringUtils.isNotBlank(userSchoolRelEntity.getUserGroupIds())) {
                                    userGroupIds = Arrays.stream(userSchoolRelEntity.getUserGroupIds().split(",")).map(Long::parseLong).collect(Collectors.toList());
                                }
                            }
                            userSchoolRelEntity.setUserNumber(userImportDTO.getUserNumber());
                            userSchoolRelEntity.setUsername(userImportDTO.getUsername());
                            userSchoolRelEntity.setGender(userImportDTO.getGender());
                            userSchoolRelEntity.setUserId(userEntity.getId());
                            userSchoolRelEntity.setSchoolId(schoolId);
                            userSchoolRelEntity.setUserType(1);
//                            userSchoolRelEntity.setPosition(JobTitleEnum.getCode(userImportDTO.getPosition(), schoolLanguageEnum));
                            UserGroupEntity userGroupEntity = userGroupMap.get(userImportDTO.getUserGroup());
                            if (userGroupEntity != null) {
                                if (!userGroupIds.contains(userGroupEntity.getId())) {
                                    userGroupIds.add(userGroupEntity.getId());
                                }
                            }
                            if (CollectionUtils.isNotEmpty(userGroupIds)) {
                                userSchoolRelEntity.setUserGroupIds(String.join(",", userGroupIds.stream()
                                        .map(String::valueOf).collect(Collectors.toList())));
                            }
                            if (!StringUtils.isNotBlank(userSchoolRelEntity.getUserNumber())) {
                                generateUserNumber(schoolId, userSchoolRelEntity);
                            }
                            schoolUserSaveOrUpdateList.add(userSchoolRelEntity);
                            // 保存教师信息和UserImportDTO的关系
                            userIdDtoMap.put(userEntity.getId(), userImportDTO);
                        }
                    }
                    if (CollectionUtils.isNotEmpty(schoolUserSaveOrUpdateList)) {
                        userSchoolRelService.saveOrUpdateBatch(schoolUserSaveOrUpdateList);
                        // 处理部门关联
                        for (UserSchoolRelEntity userSchoolRelEntity : schoolUserSaveOrUpdateList) {
                            if (userIdDtoMap.containsKey(userSchoolRelEntity.getUserId())) {
                                UserImportDTO userImportDTO = userIdDtoMap.get(userSchoolRelEntity.getUserId());
                                if (StringUtils.isNotBlank(userImportDTO.getDeptName())) {
                                    DeptEntity deptEntity = deptMap.get(userImportDTO.getDeptName());
                                    if (deptEntity!= null) {
                                        UserDeptRelEntity userDeptRelEntity = new UserDeptRelEntity();
                                        Long  userDeptRelId = userDeptMap.get(userSchoolRelEntity.getId());
                                        if(userDeptRelId!=null){
                                            userDeptRelEntity.setId(userDeptRelId);
                                        }

                                        userDeptRelEntity.setUserId(userSchoolRelEntity.getId());
                                        userDeptRelEntity.setDeptId(deptEntity.getId());
                                        userDeptRelEntity.setSchoolId(schoolId);
                                        userDeptRelEntity.setIsMaster(1);
                                        userDeptRelEntity.setIsAdmin(0);
                                        userDeptRelService.saveOrUpdate(userDeptRelEntity);
                                    }
                                }
                            }

                        }
                    }
                }
            }
            return correctCount;
        }
        return 0;
    }

    private UserImportDTO userImportConvert(Long schoolId, UserImportModel bo, Map<String, UserEntity> userLoginNameMap, SchoolLanguageEnum schoolLanguageEnum) {
        UserImportDTO userImportDTO = new UserImportDTO();
        UserEntity userEntity = userLoginNameMap.get(bo.getUName());
        if (userEntity != null) {
            userImportDTO.setId(userEntity.getId());
        } else {
            //新增用户需要初始化密码
            userImportDTO.setPassword(PasswordUtil.encryptPassword("123456"));
        }
        userImportDTO.setSchoolId(schoolId);
        userImportDTO.setUsername(bo.getUserName());
        userImportDTO.setDeptName(bo.getDeptName());
        userImportDTO.setMobile(bo.getPhone());
        userImportDTO.setGender(LanguageUtils.isMale(schoolLanguageEnum, bo.getGender()) ? 1 : 2);
        userImportDTO.setLoginName(bo.getUName());
        userImportDTO.setMobileHead(bo.getPhoneArea());
        userImportDTO.setUserGroup(bo.getUserGroup());
        userImportDTO.setUserNumber(bo.getUserNumber());
//        userImportDTO.setPosition(bo.getPosition());
        return userImportDTO;
    }

    private List<UserImportModel> readExcelData(MultipartFile file, SchoolLanguageEnum schoolLanguageEnum) {
        List<UserImportModel> result = new ArrayList<>();
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            switch (schoolLanguageEnum) {
                case ZH_MO:
                    UserImportZhTwListener importZhTwListener = new UserImportZhTwListener();
                    EasyExcel.read(inputStream, UserImportZhTwModel.class, importZhTwListener).sheet().headRowNumber(2).doReadSync();
                    List<UserImportZhTwModel> importZhTwModels = importZhTwListener.getDataList();
                    result = importZhTwModels.stream().map(item -> {
                        UserImportModel model = new UserImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
                case EN_US:
                    UserImportEnUsListener importEnUsListener = new UserImportEnUsListener();
                    EasyExcel.read(inputStream, UserImportEnUsModel.class, importEnUsListener).sheet().headRowNumber(2).doReadSync();
                    List<UserImportEnUsModel> importEnUsModels = importEnUsListener.getDataList();
                    result = importEnUsModels.stream().map(item -> {
                        UserImportModel model = new UserImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
                case PT_PT:
                    UserImportPtPtListener importPtPtListener = new UserImportPtPtListener();
                    EasyExcel.read(inputStream, UserImportPtPtModel.class, importPtPtListener).sheet().headRowNumber(2).doReadSync();
                    List<UserImportPtPtModel> importPtPtModels = importPtPtListener.getDataList();
                    result = importPtPtModels.stream().map(item -> {
                        UserImportModel model = new UserImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
                default:
            }
        } catch (IOException e) {
            log.error("Excel文件读取失败", e);
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.FILE_READ_ERROR));
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    @Override
    public String exportUser(Long schoolId, UserQueryReqModel reqModel) {
        List<UserDetailResModel> resList = getUserList(reqModel, schoolId);

        if (CollectionUtils.isNotEmpty(resList)) {
            String fileName = "用户信息导出.xlsx";
            String currentLanguage = LanguageUtil.getCurrentLanguage();
            if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
                List<UserExportEnModel> exportEnModels = resList.stream()
                        .map(item -> {
                            UserExportEnModel resModel = new UserExportEnModel();
                            BeanUtils.copyProperties(item, resModel);
//                            resModel.setUserType(SchoolUserTypeEnum.getValue(item.getUserType(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                            resModel.setGender(GenderEnum.getValue(item.getGender(), SchoolLanguageEnum.getDefValue(currentLanguage)));
//                            resModel.setStatus(UserStatusEnum.getValue(item.getStatus(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                            return resModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportEnModels, fileName, UserExportEnModel.class, FileTypeEnum.EXPORT, schoolId);
            } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
                List<UserExportPtModel> exportPtModels = resList.stream()
                        .map(item -> {
                            UserExportPtModel resModel = new UserExportPtModel();
                            BeanUtils.copyProperties(item, resModel);
//                            resModel.setUserType(SchoolUserTypeEnum.getValue(item.getUserType(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                            resModel.setGender(GenderEnum.getValue(item.getGender(), SchoolLanguageEnum.getDefValue(currentLanguage)));
//                            resModel.setStatus(UserStatusEnum.getValue(item.getStatus(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                            return resModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportPtModels, fileName, UserExportPtModel.class, FileTypeEnum.EXPORT, schoolId);
            } else {
                List<UserExportModel> exportMoModels = resList.stream()
                        .map(item -> {
                            UserExportModel resModel = new UserExportModel();
                            BeanUtils.copyProperties(item, resModel);
//                            resModel.setUserType(SchoolUserTypeEnum.getValue(item.getUserType(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                            resModel.setGender(GenderEnum.getValue(item.getGender(), SchoolLanguageEnum.getDefValue(currentLanguage)));
//                            resModel.setStatus(UserStatusEnum.getValue(item.getStatus(), SchoolLanguageEnum.getDefValue(currentLanguage)));
                            return resModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportMoModels, fileName, UserExportModel.class, FileTypeEnum.EXPORT, schoolId);
            }
        }
        return null;
    }
}