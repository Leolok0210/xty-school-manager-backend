package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.DeptDao;
import com.xiaotiyun.school.manager.model.entity.DeptEntity;
import com.xiaotiyun.school.manager.model.entity.SchoolEntity;
import com.xiaotiyun.school.manager.model.entity.UserDeptRelEntity;
import com.xiaotiyun.school.manager.model.entity.UserSchoolRelEntity;
import com.xiaotiyun.school.manager.model.req.DeptReqModel;
import com.xiaotiyun.school.manager.model.res.DeptResModel;
import com.xiaotiyun.school.manager.model.res.UserSchoolRelResModel;
import com.xiaotiyun.school.manager.service.DeptService;
import com.xiaotiyun.school.manager.service.SchoolService;
import com.xiaotiyun.school.manager.service.UserDeptRelService;
import com.xiaotiyun.school.manager.service.UserSchoolRelService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 部门Service实现类
 */
@Service
public class DeptServiceImpl extends ServiceImpl<DeptDao, DeptEntity> implements DeptService {

    @Autowired
    private UserDeptRelService userDeptRelService;
    @Autowired
    private UserSchoolRelService userSchoolRelService;

    @Autowired
    private SchoolService schoolService;

    @Resource
    LanguageUtil languageUtil;

//    /**
//     * 获取部门列表
//     * @param deptReqModel 查询条件
//     * @return 部门列表
//     */
//    @Override
//    public List<DeptResModel> listDepts(DeptReqModel deptReqModel) {
//        // : 实现部门列表查询逻辑
//        return null;
//    }

    /**
     * 获取部门详情
     * @param id 部门ID
     * @return 部门详情
     */
    @Override
    public DeptResModel getDeptById(Long id) {
        DeptEntity entity = this.getById(id);
        if (entity != null) {
            DeptResModel deptResModel = new DeptResModel();
            BeanUtils.copyProperties(entity, deptResModel);
            // 获取部门主管信息
            UserDeptRelEntity relEntity = userDeptRelService.getOne(Wrappers.<UserDeptRelEntity>lambdaQuery()
                    .eq(UserDeptRelEntity::getSchoolId, entity.getSchoolId())
                    .eq(UserDeptRelEntity::getDeptId, entity.getId())
                    .eq(UserDeptRelEntity::getIsAdmin, 1));
            if (relEntity != null) {
                // 如果存在主管信息，则获取主管用户信息
                UserSchoolRelEntity teacher = userSchoolRelService.getOne(Wrappers.<UserSchoolRelEntity>lambdaQuery()
                        .eq(UserSchoolRelEntity::getSchoolId, entity.getSchoolId())
                        .eq(UserSchoolRelEntity::getId, relEntity.getUserId()));
                if (teacher != null) {
                    deptResModel.setManagerId(teacher.getId());
                    deptResModel.setManagerName(teacher.getUsername());
                }
            }
            return deptResModel;
        }
        return null;
    }

    /**
     * 新增部门
     * @param deptReqModel 部门信息
     * @return 操作结果
     */
    @Override
    public Result<Boolean> saveDept(DeptReqModel deptReqModel) {
        DeptEntity parentEntity = this.getById(deptReqModel.getParentId());
        // 检查父部门是否存在
        if (parentEntity == null && deptReqModel.getParentId() != 0)
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
        // 检查父部门层级是否超过20级，从0开始算
        if (parentEntity != null && parentEntity.getLevel() >= 19)
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
        // 检查部门名称是否重复
        if (this.lambdaQuery().eq(DeptEntity::getSchoolId, deptReqModel.getSchoolId())
                .eq(DeptEntity::getName, deptReqModel.getName()).count() > 0)
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.DEPARTMENT_NAME_EXISTS));
        // 保存部门信息，并设置层级+1
        DeptEntity entity = new DeptEntity();
        BeanUtils.copyProperties(deptReqModel, entity);
        entity.setLevel(parentEntity == null ? 1 : parentEntity.getLevel() + 1);
        return Result.success(this.save(entity));
    }

    /**
     * 更新部门
     *
     * @param deptReqModel 部门信息
     * @return 操作结果
     */
    @Override
    public Result<Boolean> updateDept(DeptReqModel deptReqModel) {
        // 检查参数是否合法
        if (deptReqModel.getId() == null || deptReqModel.getId() == 0L) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
        }
        // 检查部门是否存在
        DeptEntity entity = this.getById(deptReqModel.getId());
        if (entity == null) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
        }
        entity.setParentId(deptReqModel.getParentId());
        // 检查部门名称是否重复
        if (!entity.getName().equals(deptReqModel.getName()) &&
                this.lambdaQuery().eq(DeptEntity::getSchoolId, deptReqModel.getSchoolId())
                .eq(DeptEntity::getName, deptReqModel.getName()).count() > 0) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.DEPARTMENT_NAME_EXISTS));
        }
        entity.setName(deptReqModel.getName());
        // 设置主管
        if (deptReqModel.getManagerId() != null && deptReqModel.getManagerId() != 0L) {
            // 把部门主管改为普通员工
            userDeptRelService.update(Wrappers.<UserDeptRelEntity>lambdaUpdate()
                    .eq(UserDeptRelEntity::getSchoolId, deptReqModel.getSchoolId())
                    .eq(UserDeptRelEntity::getDeptId, deptReqModel.getId())
                    .eq(UserDeptRelEntity::getIsAdmin, 1)
                    .set(UserDeptRelEntity::getIsAdmin, 0));
            // 设置为部门主管
            userDeptRelService.update(Wrappers.<UserDeptRelEntity>lambdaUpdate()
                    .eq(UserDeptRelEntity::getSchoolId, deptReqModel.getSchoolId())
                    .eq(UserDeptRelEntity::getDeptId, deptReqModel.getId())
                    .eq(UserDeptRelEntity::getUserId, deptReqModel.getManagerId())
                    .set(UserDeptRelEntity::getIsAdmin, 1));
        }
        entity.setCreateTime(null);
        return Result.success(this.updateById(entity));
    }

    /**
     * 删除部门
     *
     * @param id 部门ID
     * @return 操作结果
     */
    @Override
    public Result<Boolean> deleteDept(Long id) {
        // 检查部门是否存在
        DeptEntity entity = this.getById(id);
        if (entity == null) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
        }
        // 检查部门下是否有人
        if (userDeptRelService.count(Wrappers.<UserDeptRelEntity>lambdaQuery()
                .eq(UserDeptRelEntity::getSchoolId, entity.getSchoolId())
                .eq(UserDeptRelEntity::getDeptId, entity.getId())) > 0)
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.CANNOT_DELETE_DEPARTMENT_WITH_MEMBERS));
        // 获取需要删除部门的所有子部门ID
        List<Long> childIds = getChildIds(id, entity.getSchoolId(), new ArrayList<>());
        // 检查是否存在子部门
        if (!childIds.isEmpty()) {
            // 检查子部门下是否有人
            if (userDeptRelService.count(Wrappers.<UserDeptRelEntity>lambdaQuery()
                    .eq(UserDeptRelEntity::getSchoolId, entity.getSchoolId())
                    .in(UserDeptRelEntity::getDeptId, childIds)) > 0)
                return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.CANNOT_DELETE_DEPARTMENT_WITH_MEMBERS));
        }
        return Result.success(this.removeById(id));
    }

    private List<Long> getChildIds(Long id, Long schoolId, List<Long> childIds){
        List<Long> nowChildIds = this.lambdaQuery().select(DeptEntity::getId)
                .eq(DeptEntity::getSchoolId, schoolId)
                .eq(DeptEntity::getParentId, id)
                .list()
                .stream().map(DeptEntity::getId).collect(Collectors.toList());
        if (ObjectUtils.isNotEmpty(nowChildIds)) {
            childIds.addAll(nowChildIds);
            for (Long childId : nowChildIds) {
                getChildIds(childId, schoolId, childIds);
            }
        }
        return childIds;
    }

    /**
     * 获取部门树
     * @return 部门树结构
     */
    @Override
    public List<DeptResModel> getDeptTree(Long schoolId, boolean WithUser) {
        // 获取当前用户的学校ID
        if (schoolId == null) {
            return null;
        }
        SchoolEntity school = schoolService.getById(schoolId);
        if (school == null) {
            return null;
        }
        // 查询所有部门数据
        List<DeptEntity> entities = this.list(Wrappers.lambdaQuery(DeptEntity.class)
                .eq(DeptEntity::getSchoolId, schoolId));
        List<DeptResModel> allDept = entities.stream().map(entity -> {
            DeptResModel dept = new DeptResModel();
            BeanUtils.copyProperties(entity, dept);
            return dept;
        }).sorted(Comparator.comparing(DeptResModel::getLevel).reversed()).collect(Collectors.toList());
        // 倒序，用于从下级往上级算人数
        // group by 获取所有部门有多少人
        List<UserDeptRelEntity> list = userDeptRelService.list(Wrappers.<UserDeptRelEntity>lambdaQuery()
                .eq(UserDeptRelEntity::getSchoolId, schoolId));
        Map<Long, Set<Long>> userCountMap = new HashMap<>();
        Map<Long, List<UserDeptRelEntity>> deptUserMap = new HashMap<>();
        if (ObjectUtils.isNotEmpty(list)) {
            userCountMap = list.stream().collect(Collectors.groupingBy(UserDeptRelEntity::getDeptId, Collectors.mapping(UserDeptRelEntity::getUserId, Collectors.toSet())));
            deptUserMap = list.stream().collect(Collectors.groupingBy(UserDeptRelEntity::getDeptId));
        }
        // 构建id到部门的映射
        Map<Long, DeptResModel> deptMap = new HashMap<>();
        for (DeptResModel dept : allDept) {
            dept.setChildren(new ArrayList<>());
            deptMap.put(dept.getId(), dept);
        }
        // 构建树结构
        List<DeptResModel> rootDept = new ArrayList<>();
        DeptResModel root = new DeptResModel();
        root.setId(0L);
        root.setSchoolId(schoolId);
        root.setName(school.getName());
        root.setLevel(0);
        root.setChildren(new ArrayList<>());
        Set<Long> collect = list.stream().map(UserDeptRelEntity::getUserId).collect(Collectors.toSet());
        root.setUserCount(collect.size());
        rootDept.add(root);
        for (DeptResModel dept : allDept) {
            if (dept.getParentId() == 0L) {
                root.getChildren().add(dept);
            } else {
                DeptResModel parent = deptMap.get(dept.getParentId());
                if (parent != null) {
                    parent.getChildren().add(dept);
                    // 如果部门下有人员，需要累加到父级部门的人员Set
                    if (userCountMap.containsKey(dept.getId())) {
                        Set<Long> users = userCountMap.get(dept.getId());
                        Set<Long> parentUsers;
                        if (userCountMap.containsKey(parent.getId())) {
                            parentUsers = userCountMap.get(parent.getId());
                            parentUsers.addAll(users);
                        } else {
                            parentUsers = new HashSet<>(users);
                        }
                        userCountMap.put(parent.getId(), parentUsers);
                    } else {
                        userCountMap.put(dept.getId(),new HashSet<>());
                    }
                }
            }
        }
        // 装载部门人数数据
        List<UserSchoolRelEntity> allUser = userSchoolRelService.list(Wrappers.<UserSchoolRelEntity>lambdaQuery()
                .eq(UserSchoolRelEntity::getSchoolId, schoolId));
        Map<Long, UserSchoolRelEntity> idUserMap = new HashMap<>();
        if (ObjectUtils.isNotEmpty(allUser)) {
            idUserMap = allUser.stream().collect(Collectors.toMap(UserSchoolRelEntity::getId, Function.identity()));
        }
        if (WithUser) {
            for (DeptResModel dept : allDept) {
                if (userCountMap.containsKey(dept.getId())) {
                    Set<Long> deptUserIds = userCountMap.get(dept.getId());
                    dept.setUserCount(deptUserIds.size());
                }
                if (deptUserMap.containsKey(dept.getId())) {
                    List<UserDeptRelEntity> deptUserIds = deptUserMap.get(dept.getId());
                    List<UserSchoolRelResModel> userSchoolRelResModels = new ArrayList<>();
                    for (UserDeptRelEntity deptRelEntity : deptUserIds) {
                        UserSchoolRelResModel userSchoolRelResModel = new UserSchoolRelResModel();
                        userSchoolRelResModel.setTeacherId(deptRelEntity.getUserId());
                        userSchoolRelResModel.setUserId(idUserMap.get(deptRelEntity.getUserId()).getUserId());
                        userSchoolRelResModel.setUsername(idUserMap.get(deptRelEntity.getUserId()).getUsername());
                        userSchoolRelResModels.add(userSchoolRelResModel);
                    }
                    dept.setUserList(userSchoolRelResModels);
                }
            }
        } else {
            for (DeptResModel dept : allDept) {
                if (userCountMap.containsKey(dept.getId())) {
                    Set<Long> deptUserIds = userCountMap.get(dept.getId());
                    dept.setUserCount(deptUserIds.size());
                }
                if (deptUserMap.containsKey(dept.getId())) {
                    List<UserDeptRelEntity> userDeptRelEntities = deptUserMap.get(dept.getId());
                    for (UserDeptRelEntity userDeptRelEntity : userDeptRelEntities) {
                        if (userDeptRelEntity.getIsAdmin() == 1) {
                            dept.setManagerId(userDeptRelEntity.getUserId());
                            if (idUserMap.containsKey(userDeptRelEntity.getUserId())) {
                                dept.setManagerName(idUserMap.get(userDeptRelEntity.getUserId()).getUsername());
                            }
                            break;
                        }
                    }
                }
            }
        }
        return rootDept;
    }

    @Override
    public List<UserSchoolRelResModel> getDeptUsers(Long schoolId, Long deptId) {
        // 获取当前用户的学校ID
        if (schoolId == null || deptId == null) {
            return null;
        }
        // 查询所有部门数据
        List<UserDeptRelEntity> entities = userDeptRelService.list(Wrappers.<UserDeptRelEntity>lambdaQuery()
               .eq(UserDeptRelEntity::getSchoolId, schoolId)
               .eq(UserDeptRelEntity::getDeptId, deptId));
        if (ObjectUtils.isNotEmpty(entities)) {
            List<Long> userIds = entities.stream().map(UserDeptRelEntity::getUserId).collect(Collectors.toList());
            List<UserSchoolRelEntity> userEntities = userSchoolRelService.list(Wrappers.<UserSchoolRelEntity>lambdaQuery()
                  .eq(UserSchoolRelEntity::getSchoolId, schoolId)
                  .in(UserSchoolRelEntity::getId, userIds));
            if (ObjectUtils.isNotEmpty(userEntities)) {
                return userEntities.stream().map(user -> {
                    UserSchoolRelResModel userResModel = new UserSchoolRelResModel();
                    userResModel.setUserId(user.getId());
                    userResModel.setUsername(user.getUsername());
                    return userResModel;
                }).collect(Collectors.toList());
            }
        }
        return null;
    }
}