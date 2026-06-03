package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.dao.StudentDateRecordDao;
import com.xiaotiyun.school.manager.model.entity.StudentDateRecordEntity;
import com.xiaotiyun.school.manager.model.entity.StudentEntity;
import com.xiaotiyun.school.manager.model.req.StudentDateRecordUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.StudentDateRecordResModel;
import com.xiaotiyun.school.manager.service.StudentDateRecordService;
import com.xiaotiyun.school.manager.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentDateRecordServiceImpl extends ServiceImpl<StudentDateRecordDao, StudentDateRecordEntity> implements StudentDateRecordService {

    private final StudentService studentService;

    @Override
    public StudentDateRecordResModel getStudentDateRecordById(Long id, Long schoolId) {
        StudentEntity student = studentService.getById(id);
        if (student == null) {
            throw new BusinessException(LanguageConstants.STUDENT_NOT_EXIST);
        }
        List<StudentDateRecordEntity> entities = list(Wrappers.<StudentDateRecordEntity>lambdaQuery()
                .eq(StudentDateRecordEntity::getStudentId,id)
                .eq(StudentDateRecordEntity::getSchoolId,schoolId));
        if (ObjectUtils.isNotEmpty(entities)) {
            StudentDateRecordResModel resModel = new StudentDateRecordResModel();
            BeanUtils.copyProperties(entities.get(0), resModel);
            return resModel;
        } else {
            StudentDateRecordResModel resModel = new StudentDateRecordResModel();
            resModel.setId(0L);
            resModel.setSchoolId(schoolId);
            resModel.setStudentId(id);
            return resModel;
        }
    }

    @Override
    public Result<String> updateStudentDateRecord(StudentDateRecordUpdateReqModel updateEntity) {
        StudentDateRecordEntity entity = new StudentDateRecordEntity();
        BeanUtils.copyProperties(updateEntity, entity);
        List<StudentDateRecordEntity> list = list(Wrappers.<StudentDateRecordEntity>lambdaQuery()
                .eq(StudentDateRecordEntity::getStudentId, updateEntity.getStudentId())
                .eq(StudentDateRecordEntity::getSchoolId, updateEntity.getSchoolId()));
        if (ObjectUtils.isNotEmpty(list)){
            entity.setId(list.get(0).getId());
            updateById(entity);
        } else {
            entity.setId(0L);
            entity.setDeleted(0L);
            save(entity);
        }
        return Result.success();
    }

}