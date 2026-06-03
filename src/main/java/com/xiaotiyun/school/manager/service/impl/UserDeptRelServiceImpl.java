package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.UserDeptRelDao;
import com.xiaotiyun.school.manager.model.entity.DeptEntity;
import com.xiaotiyun.school.manager.model.entity.UserDeptRelEntity;
import com.xiaotiyun.school.manager.model.entity.UserSchoolRelEntity;
import com.xiaotiyun.school.manager.model.req.UserAddReqModel;
import com.xiaotiyun.school.manager.model.req.UserDeptAddReqModel;
import com.xiaotiyun.school.manager.model.req.UserUpdateReqModel;
import com.xiaotiyun.school.manager.service.DeptService;
import com.xiaotiyun.school.manager.service.UserDeptRelService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户部门关联 Service 接口实现
 */
@Service
public class UserDeptRelServiceImpl extends ServiceImpl<UserDeptRelDao, UserDeptRelEntity> implements UserDeptRelService {

    @Autowired
    private DeptService deptService;

    @Resource
    LanguageUtil languageUtil;

    @Override
    public void saveUserDept(UserAddReqModel reqModel, long schoolId, UserSchoolRelEntity userSchool){
        // 处理部门
        if (reqModel.getMasterDept()!= null) {
            // 检查部门信息
            DeptEntity dept = deptService.getById(reqModel.getMasterDept().getId());
            if (dept == null) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
            }
            // 处理主部门
            saveMasterDept(reqModel.getMasterDept(), schoolId, userSchool);
        }
        // 处理兼职部门
        if (CollectionUtils.isNotEmpty(reqModel.getSlaveDeptList())) {
            // 校验部门合法性
            checkDept(reqModel.getSlaveDeptList());
            // 保存兼职部门
            saveSlaveDept(reqModel.getSlaveDeptList(), schoolId, userSchool);
        }
    }

    @Override
    public void updateUserDept(UserUpdateReqModel reqModel, long schoolId, UserSchoolRelEntity userSchool){
        // 删除所有原有部门
        this.remove(Wrappers.<UserDeptRelEntity>lambdaQuery()
                .eq(UserDeptRelEntity::getSchoolId, schoolId)
                .eq(UserDeptRelEntity::getUserId, userSchool.getId()));
        // 重新保存部门
        if (reqModel.getMasterDept()!= null) {
            // 检查部门信息
            DeptEntity dept = deptService.getById(reqModel.getMasterDept().getId());
            if (dept == null) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
            }
            // 处理主部门
            saveMasterDept(reqModel.getMasterDept(), schoolId, userSchool);
        }
        // 处理兼职部门
        if (CollectionUtils.isNotEmpty(reqModel.getSlaveDeptList())) {
            // 校验部门合法性
            checkDept(reqModel.getSlaveDeptList());
            // 保存兼职部门
            saveSlaveDept(reqModel.getSlaveDeptList(), schoolId, userSchool);
        }
    }

    private void checkDept(List<UserDeptAddReqModel> slaveDeptList) {
        List<Long> deptIds = slaveDeptList.stream().map(UserDeptAddReqModel::getId).collect(Collectors.toList());
        List<DeptEntity> deptList = deptService.listByIds(deptIds);
        if (CollectionUtils.isEmpty(deptList) || deptList.size() != slaveDeptList.size()) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
        }
    }

    private void saveMasterDept(UserDeptAddReqModel deptModel, long schoolId, UserSchoolRelEntity userSchool) {
        UserDeptRelEntity masterDeptRel = new UserDeptRelEntity();
        masterDeptRel.setUserId(userSchool.getId());
        masterDeptRel.setSchoolId(schoolId);
        masterDeptRel.setDeptId(deptModel.getId());
        masterDeptRel.setIsAdmin(deptModel.getIsLeader());
        masterDeptRel.setIsMaster(1);
        if (deptModel.getIsLeader() == 1) {
            updateOtherLeader(deptModel.getId(), schoolId);
        }
        this.save(masterDeptRel);
    }

    private void saveSlaveDept(List<UserDeptAddReqModel> slaveDeptList, long schoolId, UserSchoolRelEntity userSchool) {
        List<UserDeptRelEntity> insertList = new ArrayList<>();
        for (UserDeptAddReqModel slaveDept : slaveDeptList) {
            UserDeptRelEntity slaveDeptRel = new UserDeptRelEntity();
            slaveDeptRel.setUserId(userSchool.getId());
            slaveDeptRel.setDeptId(slaveDept.getId());
            slaveDeptRel.setSchoolId(schoolId);
            slaveDeptRel.setIsAdmin(slaveDept.getIsLeader());
            slaveDeptRel.setIsMaster(0);
            if (slaveDept.getIsLeader() == 1) {
                updateOtherLeader(slaveDept.getId(), schoolId);
            }
            insertList.add(slaveDeptRel);
        }
        if (!insertList.isEmpty()) {
            this.saveBatch(insertList);
        }
    }

    private void updateOtherLeader(Long deptId, long schoolId) {
        this.update(Wrappers.<UserDeptRelEntity>lambdaUpdate()
                .eq(UserDeptRelEntity::getSchoolId, schoolId)
                .eq(UserDeptRelEntity::getDeptId, deptId)
                .eq(UserDeptRelEntity::getIsAdmin, 1)
                .set(UserDeptRelEntity::getIsAdmin, 0));
    }
}
