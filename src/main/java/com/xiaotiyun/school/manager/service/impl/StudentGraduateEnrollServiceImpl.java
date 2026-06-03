package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.dao.StudentGraduateEnrollMapper;
import com.xiaotiyun.school.manager.helper.UserAuthHelper;
import com.xiaotiyun.school.manager.model.entity.StudentGraduateEnrollEntity;
import com.xiaotiyun.school.manager.model.req.StudentGraduateEnrollBatcheSaveDetailsReqModel;
import com.xiaotiyun.school.manager.model.req.StudentGraduateEnrollBatcheSaveReqModel;
import com.xiaotiyun.school.manager.model.req.StudentGraduateEnrollPageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentGraduateEnrollSaveReqModel;
import com.xiaotiyun.school.manager.model.res.StudentGraduateEnrollPageResModel;
import com.xiaotiyun.school.manager.model.res.StudentGraduateEnrollResModel;
import com.xiaotiyun.school.manager.service.StudentGraduateEnrollService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentGraduateEnrollServiceImpl extends ServiceImpl<StudentGraduateEnrollMapper, StudentGraduateEnrollEntity> implements StudentGraduateEnrollService {

    @Resource
    private UserAuthHelper userAuthHelper;
    @Override
    public PageInfo<StudentGraduateEnrollPageResModel> page(StudentGraduateEnrollPageReqModel reqModel) {
        boolean commonUser = userAuthHelper.getCommonUser(reqModel.getUserId(), reqModel.getSchoolId());
        List<Long> classIds = null;
        if(commonUser)
        {
            classIds = userAuthHelper.getUserClassIds(reqModel.getUserId(), reqModel.getSchoolId());
            if(CollectionUtils.isEmpty(classIds))
            {
                PageInfo<StudentGraduateEnrollPageResModel> pageInfo = new PageInfo<>();
                pageInfo.setList(new ArrayList<>());
                return pageInfo;
            }
            reqModel.setClassIds(classIds);
        }
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<StudentGraduateEnrollPageResModel> list = this.getBaseMapper().page(reqModel);
        return new PageInfo<>(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(StudentGraduateEnrollSaveReqModel reqModel) {
        StudentGraduateEnrollEntity entity = BeanConvertUtil.convert(reqModel, StudentGraduateEnrollEntity.class);
        this.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(StudentGraduateEnrollBatcheSaveReqModel reqModel) {
        if (CollectionUtils.isNotEmpty(reqModel.getDetails())) {
            List<StudentGraduateEnrollEntity> saveList = new ArrayList<>();
            for (StudentGraduateEnrollBatcheSaveDetailsReqModel detailsReqModel : reqModel.getDetails()) {
                StudentGraduateEnrollEntity entity = BeanConvertUtil.convert(detailsReqModel, StudentGraduateEnrollEntity.class);
                entity.setSchoolId(reqModel.getSchoolId());
                entity.setClassId(reqModel.getClassId());
                saveList.add(entity);
            }
            this.saveBatch(saveList);
        }
    }

    @Override
    public List<Long> studentList(Long classId) {
        QueryWrapper<StudentGraduateEnrollEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(StudentGraduateEnrollEntity::getClassId, classId);
        List<StudentGraduateEnrollEntity> list = this.list(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.stream().map(StudentGraduateEnrollEntity::getStudentId).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, StudentGraduateEnrollSaveReqModel reqModel) {
        StudentGraduateEnrollEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        // 使用BeanUtils替代BeanConvertUtil
        BeanUtils.copyProperties(reqModel, entity);
        this.updateById(entity);
    }

    @Override
    public StudentGraduateEnrollResModel info(Long id) {
        StudentGraduateEnrollEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        return BeanConvertUtil.convert(entity, StudentGraduateEnrollResModel.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        StudentGraduateEnrollEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        this.removeById(id);
    }
}