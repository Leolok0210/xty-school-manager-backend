package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.dao.StudentMedicalAttentionMapper;
import com.xiaotiyun.school.manager.model.entity.StudentEntity;
import com.xiaotiyun.school.manager.model.entity.StudentMedicalAttentionEntity;
import com.xiaotiyun.school.manager.model.req.StudentHealthDeclareAddReqModel;
import com.xiaotiyun.school.manager.model.req.StudentMedicalAttentionReqModel;
import com.xiaotiyun.school.manager.model.req.StudentMedicalAttentionUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.StudentMedicalAttentionResModel;
import com.xiaotiyun.school.manager.service.StudentMedicalAttentionService;
import com.xiaotiyun.school.manager.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentMedicalAttentionServiceImpl extends ServiceImpl<StudentMedicalAttentionMapper, StudentMedicalAttentionEntity> implements StudentMedicalAttentionService {


    private final StudentService studentService;

    /**
     * 根据请求参数查询学生医护注意事项列表
     *
     * @param reqModel 请求参数对象
     * @return 包含学生医护注意事项列表的结果对象
     */
    @Override
    public Result<StudentMedicalAttentionResModel> listStudentMedicalAttentions(StudentMedicalAttentionReqModel reqModel) {
        // 查询条件
        QueryWrapper<StudentMedicalAttentionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", reqModel.getStudentId());
        queryWrapper.eq(reqModel.getSchoolId() != null,"school_id", reqModel.getSchoolId());
        // 结果处理
        List<StudentMedicalAttentionEntity> list = list(queryWrapper);
        if (ObjectUtils.isNotEmpty(list)) {
            StudentMedicalAttentionResModel resModel = new StudentMedicalAttentionResModel();
            BeanUtils.copyProperties(list.get(0), resModel);
            return Result.success(resModel);
        }else {// 若不存在，需要新建一个空的，给前端展示
            StudentEntity studentEntity = studentService.getById(reqModel.getStudentId());
            if (ObjectUtils.isNotEmpty(studentEntity)) {
                return Result.success(StudentMedicalAttentionResModel.builder()
                        .id(0L)
                        .classId(studentEntity.getClassId())
                        .studentName(studentEntity.getChineseName())
                        .studentId(studentEntity.getId())
                        .build());
            }
            throw new BusinessException(LanguageConstants.STUDENT_NOT_EXIST);
        }
    }

    /**
     * 更新学生医护注意事项
     *
     * @param updateEntity 更新实体对象
     * @return 操作结果对象
     */
    @Override
    public Result<String> updateStudentMedicalAttention(StudentMedicalAttentionUpdateReqModel updateEntity, Long schoolId) {
        StudentMedicalAttentionEntity entity = new StudentMedicalAttentionEntity();
        BeanUtils.copyProperties(updateEntity, entity);
        List<StudentMedicalAttentionEntity> list = list(Wrappers.<StudentMedicalAttentionEntity>lambdaQuery()
                .eq(StudentMedicalAttentionEntity::getStudentId, entity.getStudentId())
                .eq(StudentMedicalAttentionEntity::getSchoolId, schoolId));
        if (ObjectUtils.isNotEmpty(list)){
            entity.setId(list.get(0).getId());
            updateById(entity);
        } else {
            entity.setId(0L);
            entity.setDeleted(0L);
            entity.setSchoolId(schoolId);
            save(entity);
        }
        return Result.success();
    }

    @Override
    public void updateByHealthDeclare(StudentHealthDeclareAddReqModel reqModel) {
        StudentMedicalAttentionEntity entity = getById(reqModel.getStudentId());
        if (ObjectUtils.isNotEmpty(entity)) {
            entity.setSeriousChronicDisease(reqModel.getSeriousChronicDisease());
            entity.setAllergy(reqModel.getAllergy());
            entity.setIsTreating(reqModel.getIsTreating());
            entity.setIsHospitalized(reqModel.getIsHospitalized());
            updateById(entity);
        } else {
            entity = new StudentMedicalAttentionEntity();
            entity.setSeriousChronicDisease(reqModel.getSeriousChronicDisease());
            entity.setAllergy(reqModel.getAllergy());
            entity.setIsTreating(reqModel.getIsTreating());
            entity.setIsHospitalized(reqModel.getIsHospitalized());
            entity.setSchoolId(reqModel.getSchoolId());
            entity.setStudentId(reqModel.getStudentId());
            entity.setStudentName(reqModel.getStudentName());
            entity.setClassId(reqModel.getClassId());
            save(entity);
        }
    }
}