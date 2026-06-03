package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.dao.SubjectRelDao;
import com.xiaotiyun.school.manager.model.entity.SubjectRelEntity;
import com.xiaotiyun.school.manager.model.req.SubjectRelGroupQueryReqModel;
import com.xiaotiyun.school.manager.model.res.SubjectRelResModel;
import com.xiaotiyun.school.manager.service.SubjectRelService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Service
public class SubjectRelServiceImpl extends ServiceImpl<SubjectRelDao, SubjectRelEntity> implements SubjectRelService {
    @Override
    public List<SubjectRelResModel> listByGroup(SubjectRelGroupQueryReqModel reqModel) {
        return this.baseMapper.selectSubjectAndRelByGroup(reqModel);
    }

    @Override
    public List<SubjectRelResModel> listByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids))
        {
            return Collections.emptyList();
        }
        return this.baseMapper.selectSubjectAndRelByIds(ids);
    }
}