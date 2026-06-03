package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.dao.TranScriptDetailsDao;
import com.xiaotiyun.school.manager.model.entity.TranscriptDetailsEntity;
import com.xiaotiyun.school.manager.model.req.TranScriptDetailsQueryReqModel;
import com.xiaotiyun.school.manager.model.req.TranScriptDetailsSaveReqModel;
import com.xiaotiyun.school.manager.model.res.TranScriptDetailsResModel;
import com.xiaotiyun.school.manager.service.TranScriptDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TranScriptDetailsServiceImpl extends ServiceImpl<TranScriptDetailsDao, TranscriptDetailsEntity> implements TranScriptDetailsService {

    @Override
    public List<TranScriptDetailsResModel> list(TranScriptDetailsQueryReqModel reqModel, Long schoolId) {

        LambdaQueryWrapper<TranscriptDetailsEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TranscriptDetailsEntity::getSchoolId, schoolId).
                eq(TranscriptDetailsEntity::getClassId, reqModel.getClassId());

        List<TranscriptDetailsEntity> list = this.list(wrapper);
        
        List<TranScriptDetailsResModel> resList = list.stream().map(entity -> {
            TranScriptDetailsResModel resModel = new TranScriptDetailsResModel();
            BeanUtils.copyProperties(entity, resModel);
            return resModel;
        }).collect(Collectors.toList());
        
        return resList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveOrUpdate(TranScriptDetailsSaveReqModel reqModel, Long schoolId) {
        // 根据 studentId 查询是否存在记录
        TranscriptDetailsEntity existingRecord = this.lambdaQuery()
            .eq(TranscriptDetailsEntity::getStudentId, reqModel.getStudentId())
            .eq(TranscriptDetailsEntity::getSchoolId, schoolId)
            .one();
        
        TranscriptDetailsEntity entity = new TranscriptDetailsEntity();
        BeanUtils.copyProperties(reqModel, entity);
        entity.setSchoolId(schoolId);
        
        if (existingRecord != null) {
            // 更新操作
            entity.setId(existingRecord.getId());
            entity.setUpdateTime(LocalDateTime.now());
            return this.updateById(entity);
        } else {
            // 新增操作
            return this.save(entity);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id, Long schoolId) {
        // 使用 lambdaUpdate 构建条件并执行逻辑删除
        boolean success = this.lambdaUpdate()
                .eq(TranscriptDetailsEntity::getId, id)
                .eq(TranscriptDetailsEntity::getSchoolId, schoolId)
                .remove();

        return success;
    }

    @Override
    public TranScriptDetailsResModel info(Long id, Long schoolId) {

        TranscriptDetailsEntity entity = this.getById(id);

        // 校验学校ID
        if (!schoolId.equals(entity.getSchoolId())) {
            throw new BusinessException("无权操作其他学校的数据");
        }
        TranScriptDetailsResModel resModel = new TranScriptDetailsResModel();
        BeanUtils.copyProperties(entity, resModel);
        return resModel;
    }
}