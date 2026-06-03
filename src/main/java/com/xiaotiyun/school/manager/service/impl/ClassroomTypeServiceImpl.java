package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.dao.ClassroomTypeDao;
import com.xiaotiyun.school.manager.model.entity.ClassroomTypeEntity;
import com.xiaotiyun.school.manager.model.req.ClassroomTypeReqModel;
import com.xiaotiyun.school.manager.model.res.ClassroomTypeResModel;
import com.xiaotiyun.school.manager.service.ClassroomTypeService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassroomTypeServiceImpl extends ServiceImpl<ClassroomTypeDao, ClassroomTypeEntity> implements ClassroomTypeService {

    @Override
    @Transactional
    public Long add(Long schoolId, ClassroomTypeReqModel reqModel) {
        ClassroomTypeEntity entity = BeanConvertUtil.convert(reqModel, ClassroomTypeEntity.class);
        entity.setSchoolId(schoolId);
        this.save(entity);
        return entity.getId();
    }

    @Override
    @Transactional
    public void update(Long id, ClassroomTypeReqModel reqModel) {
        ClassroomTypeEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        if (entity.getIsSystem()) {
            throw new BusinessException(LanguageConstants.PRESET_PARAM_MODIFY);
        }
        BeanUtils.copyProperties(reqModel, entity);
        this.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ClassroomTypeEntity entity = getById(id);
        if (entity != null) {
            if (entity.getIsSystem()) {
                throw new BusinessException(LanguageConstants.PRESET_PARAM_DELETE);
            }
            this.removeById(id);
        }
    }

    @Override
    public List<ClassroomTypeResModel> list(Long schoolId) {
        List<ClassroomTypeResModel> result = new ArrayList<>();
        QueryWrapper<ClassroomTypeEntity> wrapper = new QueryWrapper<>();
        //获取系统预设类型
        wrapper.lambda().eq(ClassroomTypeEntity::getIsSystem, true);
        List<ClassroomTypeEntity> presetList = this.list(wrapper);
        if (CollectionUtils.isNotEmpty(presetList)) {
            presetList.forEach(entity -> {
                ClassroomTypeResModel resModel = new ClassroomTypeResModel();
                BeanUtils.copyProperties(entity, resModel);
                result.add(resModel);
            });
        }
        QueryWrapper<ClassroomTypeEntity> queryWrapper = new QueryWrapper<>();
        //获取学校设置的类型
        queryWrapper.lambda().eq(ClassroomTypeEntity::getSchoolId, schoolId);
        List<ClassroomTypeEntity> list = this.list(queryWrapper.lambda().orderByDesc(ClassroomTypeEntity::getCreateTime));
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(entity -> {
                ClassroomTypeResModel resModel = new ClassroomTypeResModel();
                BeanUtils.copyProperties(entity, resModel);
                result.add(resModel);
            });
        }
        return result;
    }
}