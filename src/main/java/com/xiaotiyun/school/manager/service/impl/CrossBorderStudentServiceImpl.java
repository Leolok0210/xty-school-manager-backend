package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.dao.CrossBorderStudentDao;
import com.xiaotiyun.school.manager.model.entity.CrossBorderStudentEntity;
import com.xiaotiyun.school.manager.model.req.CrossBorderStudentReqModel;
import com.xiaotiyun.school.manager.model.res.CrossBorderStudentResModel;
import com.xiaotiyun.school.manager.service.CrossBorderStudentService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 跨境学生登记服务实现类
 */
@Service
public class CrossBorderStudentServiceImpl extends ServiceImpl<CrossBorderStudentDao, CrossBorderStudentEntity> implements CrossBorderStudentService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(CrossBorderStudentReqModel reqModel) {
        QueryWrapper<CrossBorderStudentEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(CrossBorderStudentEntity::getStudentId, reqModel.getStudentId());
        List<CrossBorderStudentEntity> list = this.list(wrapper);
        CrossBorderStudentEntity entity;
        if (CollectionUtils.isNotEmpty(list)) {
            entity = list.get(0);
        } else {
            entity = new CrossBorderStudentEntity();
        }
        BeanUtils.copyProperties(reqModel, entity);
        this.saveOrUpdate(entity);
        return entity.getId();
    }

    @Override
    public void update(Long studentId, CrossBorderStudentReqModel reqModel) {
        QueryWrapper<CrossBorderStudentEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(CrossBorderStudentEntity::getStudentId, studentId);
        List<CrossBorderStudentEntity> list = this.list(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            CrossBorderStudentEntity entity = list.get(0);
            BeanUtils.copyProperties(reqModel, entity);
            this.updateById(entity);
        }
    }

    @Override
    public CrossBorderStudentResModel info(Long studentId) {
        CrossBorderStudentResModel resModel = new CrossBorderStudentResModel();
        QueryWrapper<CrossBorderStudentEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(CrossBorderStudentEntity::getStudentId, studentId);
        List<CrossBorderStudentEntity> list = this.list(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            BeanUtils.copyProperties(list.get(0), resModel);
        }
        return resModel;
    }
}