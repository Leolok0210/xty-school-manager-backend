package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.dao.StudentEnrollMapper;
import com.xiaotiyun.school.manager.model.entity.StudentEnrollEntity;
import com.xiaotiyun.school.manager.model.req.StudentEnrollSaveReqModel;
import com.xiaotiyun.school.manager.model.res.StudentEnrollResModel;
import com.xiaotiyun.school.manager.service.StudentEnrollService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class StudentEnrollServiceImpl extends ServiceImpl<StudentEnrollMapper, StudentEnrollEntity> implements StudentEnrollService {

    @Override
    public StudentEnrollResModel info(Long studentId) {
        LambdaQueryWrapper<StudentEnrollEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentEnrollEntity::getStudentId, studentId)
                .eq(StudentEnrollEntity::getDeleted, 0);
        StudentEnrollEntity entity = this.getOne(wrapper);
        if (entity == null) {
            return null;
        }
        return BeanConvertUtil.convert(entity, StudentEnrollResModel.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addOrEdit(StudentEnrollSaveReqModel reqModel) {
        // 检查是否已存在
        StudentEnrollEntity entity = BeanConvertUtil.convert(reqModel, StudentEnrollEntity.class);
        LambdaQueryWrapper<StudentEnrollEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentEnrollEntity::getStudentId, reqModel.getStudentId())
                .eq(StudentEnrollEntity::getDeleted, 0);
        StudentEnrollEntity studentEnroll = this.getOne(wrapper);
        if (studentEnroll != null) {
            entity.setId(studentEnroll.getId());
        }
        this.saveOrUpdate(entity);
    }
}