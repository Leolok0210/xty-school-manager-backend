package com.xiaotiyun.school.manager.helper;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.service.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserAuthHelper {


    @Resource
    private UserService userService;

    @Resource
    private UserClassRelService userClassRelService;

    @Resource
    private SysClassService sysClassService;

    @Resource
    private TeachingSettingService teachingSettingService;

    @Resource
    private UserSchoolRelService userSchoolRelService;


    /**
     * 查看改用户是否有班级权限
     *
     * @param userId
     * @param classId
     * @return
     */
    public Map<Long, Boolean> hasClassPermission(Long userId, Long schoolId, List<Long> classId) {
        if (classId == null || classId.isEmpty()) {
            return Collections.emptyMap();
        }
        //classId to map
        Map<Long, Boolean> classIdMap = classId.stream().collect(Collectors.toMap(aclass -> aclass, aclass -> true));
        Integer userRole = userService.getUserRole(userId, schoolId);
        if (userRole != 1) {
            return classIdMap;
        }
        //如果是普通用户需要校验权限
        List<Long> userClassIds = getUserClassIds(userId, schoolId);
        //to map
        Map<Long, Boolean> userClassIdMap = userClassIds.stream().collect(Collectors.toMap(aclass -> aclass, aclass -> true));
        for (Long id : classId) {
            //查看班级权限表里面是否有数据
            if (!userClassIdMap.containsKey(id)) {
                classIdMap.put(id, false);
            }
        }
        return classIdMap;
    }

    public List<Long> getUserClassIds(Long userId, Long schoolId) {
        List<Long> classIds = new ArrayList<>();
        LambdaQueryWrapper<UserSchoolRelEntity> schoolRelWrapper = new LambdaQueryWrapper<>();
        schoolRelWrapper.eq(UserSchoolRelEntity::getUserId, userId);
        schoolRelWrapper.eq(UserSchoolRelEntity::getSchoolId, schoolId);
        UserSchoolRelEntity userSchoolRel = userSchoolRelService.getOne(schoolRelWrapper);
        if (userSchoolRel == null) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<UserClassRelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserClassRelEntity::getUserId, userSchoolRel.getId())
                .eq(UserClassRelEntity::getSchoolId, schoolId);
        List<UserClassRelEntity> list = userClassRelService.list(queryWrapper);
        List<Long> departments = list.stream().filter(item -> item.getType() == 1).map(UserClassRelEntity::getRelId).collect(Collectors.toList());
        List<Long> groups = list.stream().filter(item -> item.getType() == 2).map(UserClassRelEntity::getRelId).collect(Collectors.toList());
        List<Long> classes = list.stream().filter(item -> item.getType() == 3).map(UserClassRelEntity::getRelId).collect(Collectors.toList());
        LambdaQueryWrapper<SysClass> classLambdaQueryWrapper = new LambdaQueryWrapper<>();
        classLambdaQueryWrapper.eq(SysClass::getSchoolId, schoolId)
                .and(item -> item.in(!CollectionUtils.isEmpty(groups), SysClass::getGradeGroup, groups)
                        .or().in(!CollectionUtils.isEmpty(departments), SysClass::getDepartment, departments)
                        .or().eq(SysClass::getHeadTeacher, userSchoolRel.getId()));
        List<SysClass> classList = sysClassService.list(classLambdaQueryWrapper);
        if (!CollectionUtils.isEmpty(classList)) {
            classIds = classList.stream().map(SysClass::getId).collect(Collectors.toList());
        }
        if (!CollectionUtils.isEmpty(classes)) {
            classIds.addAll(classes);
        }
        //任课教师
        LambdaQueryWrapper<TeachingSetting> teachingSettingLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teachingSettingLambdaQueryWrapper.eq(TeachingSetting::getSchoolId, schoolId)
                .eq(TeachingSetting::getTeacherId, userSchoolRel.getId());

        List<TeachingSetting> teachingSettings = teachingSettingService.list(teachingSettingLambdaQueryWrapper);
        if (!CollectionUtils.isEmpty(teachingSettings)) {
            classIds.addAll(teachingSettings.stream().map(TeachingSetting::getClassId).collect(Collectors.toList()));
        }
        //去重
        classIds = classIds.stream().distinct().collect(Collectors.toList());
        return classIds;
    }

    public List<Long> getUserGrades(Long userId, Long schoolId) {
        List<Long> gradeIds = new ArrayList<>();
        LambdaQueryWrapper<UserSchoolRelEntity> schoolRelWrapper = new LambdaQueryWrapper<>();
        schoolRelWrapper.eq(UserSchoolRelEntity::getUserId, userId);
        schoolRelWrapper.eq(UserSchoolRelEntity::getSchoolId, schoolId);
        UserSchoolRelEntity userSchoolRel = userSchoolRelService.getOne(schoolRelWrapper);
        if (userSchoolRel == null) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<UserClassRelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserClassRelEntity::getUserId, userSchoolRel.getId())
                .eq(UserClassRelEntity::getSchoolId, schoolId);
        List<UserClassRelEntity> list = userClassRelService.list(queryWrapper);
        List<Long> departments = list.stream().filter(item -> item.getType() == 1).map(UserClassRelEntity::getRelId).collect(Collectors.toList());
        List<Long> groups = list.stream().filter(item -> item.getType() == 2).map(UserClassRelEntity::getRelId).collect(Collectors.toList());
        List<Long> classes = list.stream().filter(item -> item.getType() == 3).map(UserClassRelEntity::getRelId).collect(Collectors.toList());
        LambdaQueryWrapper<SysClass> classLambdaQueryWrapper = new LambdaQueryWrapper<>();
        classLambdaQueryWrapper.eq(SysClass::getSchoolId, schoolId)
                .and(item -> item.in(!CollectionUtils.isEmpty(classes), SysClass::getId, classes)
                        .or().in(!CollectionUtils.isEmpty(departments), SysClass::getDepartment, departments)
                        .or().eq(SysClass::getHeadTeacher, userId));
        List<SysClass> classList = sysClassService.list(classLambdaQueryWrapper);
        if (!CollectionUtils.isEmpty(classList)) {
            gradeIds = classList.stream().map(SysClass::getGradeGroup).distinct().collect(Collectors.toList());
        }
        if (!CollectionUtils.isEmpty(groups)) {
            gradeIds.addAll(groups);
        }
        //任课教师
        LambdaQueryWrapper<TeachingSetting> teachingSettingLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teachingSettingLambdaQueryWrapper.eq(TeachingSetting::getSchoolId, schoolId)
                .eq(TeachingSetting::getTeacherId, userId);

        List<TeachingSetting> teachingSettings = teachingSettingService.list(teachingSettingLambdaQueryWrapper);
        if (!CollectionUtils.isEmpty(teachingSettings)) {
            List<Long> classIds = teachingSettings.stream().map(TeachingSetting::getClassId).distinct().collect(Collectors.toList());
            List<SysClass> sysClasses = sysClassService.listByIds(classIds);
            if (!CollectionUtils.isEmpty(sysClasses)) {
                gradeIds.addAll(sysClasses.stream().map(SysClass::getGradeGroup).distinct().collect(Collectors.toList()));
            }
        }
        return gradeIds;
    }

    public boolean getCommonUser(Long userId, Long schoolId) {
        // 获取学生，如果拿不到则不是学生端
        StudentEntity student = (StudentEntity) StpUtil.getSession().get("student");
        // 是学生端时，不校验权限
        if (student != null) return false;
        Integer userRole = userService.getUserRole(userId, schoolId);
        return userRole == 1;
    }


}
