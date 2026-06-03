package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.dao.StudentFamilyMapper;
import com.xiaotiyun.school.manager.model.entity.StudentFamilyEntity;
import com.xiaotiyun.school.manager.model.req.StudentFamilySaveReqModel;
import com.xiaotiyun.school.manager.model.req.StudentHealthDeclareAddReqModel;
import com.xiaotiyun.school.manager.model.res.StudentFamilyResModel;
import com.xiaotiyun.school.manager.service.StudentFamilyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;

import java.util.List;

@Slf4j
@Service
public class StudentFamilyServiceImpl extends ServiceImpl<StudentFamilyMapper, StudentFamilyEntity> implements StudentFamilyService {

    @Override
    public StudentFamilyResModel getByStudentId(Long studentId) {
        LambdaQueryWrapper<StudentFamilyEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentFamilyEntity::getStudentId, studentId)
                .eq(StudentFamilyEntity::getDeleted, 0);
        StudentFamilyEntity entity = this.getOne(wrapper);
        if (entity == null) {
            return null;
        }
        return BeanConvertUtil.convert(entity, StudentFamilyResModel.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Long studentId, StudentFamilySaveReqModel reqModel) {
        // 检查是否已存在
        LambdaQueryWrapper<StudentFamilyEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentFamilyEntity::getStudentId, studentId)
                .eq(StudentFamilyEntity::getDeleted, 0);
        if (this.count(wrapper) > 0) {
            throw new BusinessException(LanguageConstants.STUDENT_FAMILY_EXISTS);
        }

        StudentFamilyEntity entity = BeanConvertUtil.convert(reqModel, StudentFamilyEntity.class);
        entity.setStudentId(studentId);
        this.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long studentId, StudentFamilySaveReqModel reqModel) {
        LambdaQueryWrapper<StudentFamilyEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentFamilyEntity::getStudentId, studentId)
                .eq(StudentFamilyEntity::getDeleted, 0);
        List<StudentFamilyEntity> familyEntities = this.list(wrapper);
        if (CollectionUtils.isNotEmpty(familyEntities)) {
            StudentFamilyEntity entity = familyEntities.get(0);
            BeanUtils.copyProperties(reqModel, entity);
            this.updateById(entity);
        }
    }

    @Override
    public void updateByHealthDeclare(StudentHealthDeclareAddReqModel reqModel) {
        List<StudentFamilyEntity> entity = list(Wrappers.<StudentFamilyEntity>lambdaQuery()
                .eq(StudentFamilyEntity::getStudentId, reqModel.getStudentId())
                .eq(StudentFamilyEntity::getDeleted, 0));
        if (CollectionUtils.isNotEmpty(entity)) {
            StudentFamilyEntity familyEntity = entity.get(0);
            getFamilyEntity(reqModel, familyEntity);
            updateById(familyEntity);
        } else {
            StudentFamilyEntity familyEntity = new StudentFamilyEntity();
            familyEntity.setStudentId(reqModel.getStudentId());
            getFamilyEntity(reqModel, familyEntity);
            this.save(familyEntity);
        }
    }

    private void getFamilyEntity(StudentHealthDeclareAddReqModel reqModel, StudentFamilyEntity familyEntity) {
        familyEntity.setEmergencyContact(reqModel.getEmergencyContactName());
        familyEntity.setEmergencyRelation(reqModel.getEmergencyContactRelation());
        familyEntity.setEmergencyPhone(reqModel.getEmergencyContactPhone());
        if (ObjectUtils.isNotEmpty(reqModel.getEmergencyContactNameTwo())) {
            familyEntity.setSecondEmergencyContact(reqModel.getEmergencyContactNameTwo());
        }
        if (ObjectUtils.isNotEmpty(reqModel.getEmergencyContactRelationTwo())) {
            familyEntity.setSecondEmergencyRelation(reqModel.getEmergencyContactRelationTwo());
        }
        if (ObjectUtils.isNotEmpty(reqModel.getEmergencyContactPhoneTwo())) {
            familyEntity.setSecondEmergencyPhone(reqModel.getEmergencyContactPhoneTwo());
        }
    }
} 