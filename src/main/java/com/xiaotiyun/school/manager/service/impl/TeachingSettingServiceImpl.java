package com.xiaotiyun.school.manager.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.UserGroupTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.UserTypeEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.dao.TeachingSettingDao;
import com.xiaotiyun.school.manager.dao.UserSchoolRelDao;
import com.xiaotiyun.school.manager.model.entity.TeachingSetting;
import com.xiaotiyun.school.manager.model.entity.UserEntity;
import com.xiaotiyun.school.manager.model.entity.UserGroupEntity;
import com.xiaotiyun.school.manager.model.entity.UserSchoolRelEntity;
import com.xiaotiyun.school.manager.model.req.TeachingSettingQueryByRoleReqModel;
import com.xiaotiyun.school.manager.model.req.TeachingSettingQueryReqModel;
import com.xiaotiyun.school.manager.model.res.TeachingSettingDetailResModel;
import com.xiaotiyun.school.manager.model.res.TeachingSettingRoleResModel;
import com.xiaotiyun.school.manager.service.TeachingSettingService;
import com.xiaotiyun.school.manager.service.UserGroupService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeachingSettingServiceImpl extends ServiceImpl<TeachingSettingDao, TeachingSetting> implements TeachingSettingService {

    @Autowired
    private TeachingSettingDao teachingSettingDao;

    @Autowired
    private UserSchoolRelDao userSchoolRelDao;

    @Autowired
    private UserGroupService userGroupService;

    @Override
    public void createTeachingSettings(List<TeachingSetting> teachingSettings) {
        teachingSettings.forEach(teachingSetting -> {
            teachingSetting.setCreateTime(LocalDateTime.now());
            teachingSetting.setUpdateTime(LocalDateTime.now());
            teachingSetting.setDeleted(0L);
        });
        saveBatch(teachingSettings);
    }

    @Override
    public void updateTeachingSetting(TeachingSetting teachingSetting) {
        teachingSetting.setUpdateTime(LocalDateTime.now());
        updateById(teachingSetting);
    }

    @Override
    public void deleteTeachingSetting(Long id) {
        //逻辑删除
        removeById(id);
    }

    @Override
    public TeachingSetting getTeachingSettingById(Long id) {
        return this.baseMapper.selectById(id);
    }

    @Override
    public PageInfo<TeachingSettingDetailResModel> getTeachingSettings(TeachingSettingQueryReqModel reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<TeachingSettingDetailResModel> teachingSettingDetailResModels = teachingSettingDao.getTeachingSettings(reqModel);
        return new PageInfo<>(teachingSettingDetailResModels);
    }

    @Override
    public PageInfo<TeachingSettingRoleResModel> getTeachingSettingsByRole(TeachingSettingQueryByRoleReqModel reqModel) {
        UserEntity userEntity = (UserEntity) StpUtil.getSession().get("userInfo");
        if (userEntity == null) {
            throw new BusinessException(LanguageConstants.USER_NOT_EXISTS);
        }
        // 超管
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        if (UserTypeEnum.isSuperAdmin(userEntity.getUserType())) {
            List<TeachingSettingRoleResModel> teachingSettingDetailResModels = teachingSettingDao.getTeachingSettingsByRole(reqModel);
            return new PageInfo<>(teachingSettingDetailResModels);
        }
        // 学校管理员
        List<UserSchoolRelEntity> users = userSchoolRelDao.selectList(Wrappers.<UserSchoolRelEntity>lambdaQuery()
                .eq(UserSchoolRelEntity::getSchoolId, reqModel.getSchoolId())
                .eq(UserSchoolRelEntity::getUserId, userEntity.getId()));
        if (!CollectionUtils.isNotEmpty(users)) {
            throw new BusinessException(LanguageConstants.USER_NOT_EXISTS);
        }
        UserSchoolRelEntity user = users.get(0);
        List<Long> userGroupIds = Arrays.stream(user.getUserGroupIds().split(",")).map(Long::parseLong).collect(Collectors.toList());
        List<UserGroupEntity> userGroups = userGroupService.listByIds(userGroupIds);
        if (CollectionUtils.isNotEmpty(userGroups)) {
            long count = userGroups.stream().filter(userGroupEntity -> StringUtils.isNotBlank(userGroupEntity.getCode()) && userGroupEntity.getCode().equals(UserGroupTypeEnum.SCHOOL_ADMIN.getCode())).count();
            if (count > 0) {
                List<TeachingSettingRoleResModel> teachingSettingDetailResModels = teachingSettingDao.getTeachingSettingsByRole(reqModel);
                return new PageInfo<>(teachingSettingDetailResModels);
            }
        }
        // 普通老师
        reqModel.setTeacherId(user.getId());
        List<TeachingSettingRoleResModel> teachingSettingDetailResModels = teachingSettingDao.getTeachingSettingsByRole(reqModel);
        return new PageInfo<>(teachingSettingDetailResModels);
    }

    @Override
    public List<TeachingSetting> getTeachingSettingsBySchoolId(Long schoolId) {
        if (schoolId != null) {
            LambdaQueryWrapper<TeachingSetting> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TeachingSetting::getSchoolId, schoolId)
                    .eq(TeachingSetting::getDeleted, 0);
            List<TeachingSetting> teachingSettings = this.baseMapper.selectList(wrapper);
            return teachingSettings;
        }
        return null;
    }
}