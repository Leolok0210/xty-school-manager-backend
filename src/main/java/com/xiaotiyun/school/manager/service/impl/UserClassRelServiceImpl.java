package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.dao.UserClassRelDao;
import com.xiaotiyun.school.manager.model.entity.GradeGroup;
import com.xiaotiyun.school.manager.model.entity.SysClass;
import com.xiaotiyun.school.manager.model.entity.UserClassRelEntity;
import com.xiaotiyun.school.manager.model.entity.UserSchoolRelEntity;
import com.xiaotiyun.school.manager.model.req.UserAddReqModel;
import com.xiaotiyun.school.manager.model.req.UserClassRelReqModel;
import com.xiaotiyun.school.manager.model.req.UserUpdateReqModel;
import com.xiaotiyun.school.manager.service.GradeGroupService;
import com.xiaotiyun.school.manager.service.SysClassService;
import com.xiaotiyun.school.manager.service.UserClassRelService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserClassRelServiceImpl extends ServiceImpl<UserClassRelDao, UserClassRelEntity> implements UserClassRelService {

    @Resource
    private GradeGroupService gradeGroupService;
    @Resource
    private SysClassService classService;

    @Override
    public void saveUserClass(UserAddReqModel reqModel, long schoolId, UserSchoolRelEntity userSchool) {
        saveUserClassEntity(reqModel.getRelList(), schoolId, userSchool);
    }

    @Override
    public void updateUserClass(UserUpdateReqModel reqModel, long schoolId, UserSchoolRelEntity userSchool) {
        // 删除所有原有班级
        this.remove(Wrappers.<UserClassRelEntity>lambdaQuery()
                .eq(UserClassRelEntity::getSchoolId, schoolId)
                .eq(UserClassRelEntity::getUserId, userSchool.getId()));
        saveUserClassEntity(reqModel.getRelList(), schoolId, userSchool);
    }

    private void saveUserClassEntity(List<UserClassRelReqModel> classRelList, long schoolId, UserSchoolRelEntity userSchool) {
        // 处理班级数据
        if (CollectionUtils.isNotEmpty(classRelList)) {
            // 验证合法性 班级级组
            List<Long> groupIds = classRelList.stream().filter(a->a.getType() == 2).map(UserClassRelReqModel::getRelId).collect(Collectors.toList());
            List<Long> classIds = classRelList.stream().filter(a->a.getType() == 3).map(UserClassRelReqModel::getRelId).collect(Collectors.toList());
            if (!classIds.isEmpty()) {
                List<SysClass> sysClasses = classService.listByIds(classIds);
                if (sysClasses == null || sysClasses.size() != classIds.size()) {
                    throw new BusinessException(LanguageConstants.PARAM_ERROR);
                }
            }
            if (!groupIds.isEmpty()) {
                List<GradeGroup> gradeGroups = gradeGroupService.listByIds(groupIds);
                if (gradeGroups == null || gradeGroups.size()!= groupIds.size()) {
                    throw new BusinessException(LanguageConstants.PARAM_ERROR);
                }
            }
            // 处理班级数据
            List<UserClassRelEntity> insert = new ArrayList<>();
            for (UserClassRelReqModel classRel : classRelList) {
                UserClassRelEntity userClassRel = new UserClassRelEntity();
                BeanUtils.copyProperties(classRel, userClassRel);
                userClassRel.setUserId(userSchool.getId());
                userClassRel.setSchoolId(schoolId);
                insert.add(userClassRel);
            }
            if (!insert.isEmpty()) {
                this.saveBatch(insert);
            }
        }
    }
}
