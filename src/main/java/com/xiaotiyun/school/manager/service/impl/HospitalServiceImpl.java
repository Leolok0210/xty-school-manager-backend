package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.dao.HospitalDao;
import com.xiaotiyun.school.manager.model.entity.HospitalEntity;
import com.xiaotiyun.school.manager.model.req.HospitalAddReqModel;
import com.xiaotiyun.school.manager.model.req.HospitalQueryReqModel;
import com.xiaotiyun.school.manager.model.res.HospitalResModel;
import com.xiaotiyun.school.manager.service.HospitalService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HospitalServiceImpl extends ServiceImpl<HospitalDao, HospitalEntity> implements HospitalService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addHospital(HospitalAddReqModel reqModel, Long schoolId) {
        // 1. 校验医院名称是否重复
        boolean exists = this.count(new LambdaQueryWrapper<HospitalEntity>()
                .eq(HospitalEntity::getName, reqModel.getName())
                .eq(HospitalEntity::getSchoolId, schoolId)) > 0;
        if (exists) {
            throw new BusinessException(LanguageConstants.HOSPITAL_NAME_EXISTS);
        }

        // 2. 保存医院信息
        HospitalEntity entity = new HospitalEntity();
        BeanUtils.copyProperties(reqModel, entity);
        entity.setSchoolId(schoolId);
        save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHospital(Long id, HospitalAddReqModel reqModel, Long schoolId) {
        // 1. 校验医院是否存在
        HospitalEntity hospital = getById(id);
        if (hospital == null || !hospital.getSchoolId().equals(schoolId)) {
            throw new BusinessException(LanguageConstants.HOSPITAL_NOT_EXISTS);
        }

        // 2. 校验医院名称是否重复
        boolean exists = this.count(new LambdaQueryWrapper<HospitalEntity>()
                .eq(HospitalEntity::getName, reqModel.getName())
                .eq(HospitalEntity::getSchoolId, schoolId)
                .ne(HospitalEntity::getId, id)) > 0;
        if (exists) {
            throw new BusinessException(LanguageConstants.HOSPITAL_NAME_EXISTS);
        }

        // 3. 更新医院信息
        HospitalEntity entity = new HospitalEntity();
        BeanUtils.copyProperties(reqModel, entity);
        entity.setId(id);
        entity.setSchoolId(schoolId);
        updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteHospital(Long id, Long schoolId) {
        // 1. 校验医院是否存在
        HospitalEntity hospital = getById(id);
        if (hospital == null || !hospital.getSchoolId().equals(schoolId)) {
            return;
        }

        // 2. 逻辑删除
        HospitalEntity entity = new HospitalEntity();
        entity.setId(id);
        this.removeById(id);
    }

    @Override
    public HospitalResModel getHospitalDetail(Long id, Long schoolId) {
        HospitalEntity hospital = getOne(new LambdaQueryWrapper<HospitalEntity>()
                .eq(HospitalEntity::getId, id)
                .eq(HospitalEntity::getSchoolId, schoolId));
                
        if (hospital == null) {
            return null;
        }
        
        HospitalResModel resModel = new HospitalResModel();
        BeanUtils.copyProperties(hospital, resModel);
        return resModel;
    }

    @Override
    public PageInfo<HospitalResModel> getHospitalList(HospitalQueryReqModel reqModel, Long schoolId) {
        // 1. 设置分页
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<HospitalEntity> wrapper = new LambdaQueryWrapper<HospitalEntity>()
                .eq(HospitalEntity::getSchoolId, schoolId)
                .like(StringUtils.isNotBlank(reqModel.getName()), 
                      HospitalEntity::getName, reqModel.getName())
                .orderByDesc(HospitalEntity::getCreateTime);

        // 3. 查询数据并获取分页信息
        List<HospitalEntity> list = list(wrapper);
        PageInfo<HospitalEntity> pageInfo = new PageInfo<>(list);
        
        // 4. 转换返回结果
        List<HospitalResModel> resList = pageInfo.getList().stream().map(hospital -> {
            HospitalResModel resModel = new HospitalResModel();
            BeanUtils.copyProperties(hospital, resModel);
            return resModel;
        }).collect(Collectors.toList());

        // 5. 构造新的分页信息对象，同时复制分页相关属性
        PageInfo<HospitalResModel> resPageInfo = new PageInfo<>(resList);
        BeanUtils.copyProperties(pageInfo, resPageInfo, "list");
        
        return resPageInfo;
    }
} 