package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.dao.CompetitionMapper;
import com.xiaotiyun.school.manager.dao.CompetitionRecordMapper;
import com.xiaotiyun.school.manager.model.entity.CompetitionEntity;
import com.xiaotiyun.school.manager.model.req.CompetitionPageReqModel;
import com.xiaotiyun.school.manager.model.req.CompetitionSaveReqModel;
import com.xiaotiyun.school.manager.model.res.CompetitionPageResModel;
import com.xiaotiyun.school.manager.model.res.CompetitionResModel;
import com.xiaotiyun.school.manager.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitionServiceImpl extends ServiceImpl<CompetitionMapper, CompetitionEntity> implements CompetitionService {
    private final CompetitionRecordMapper competitionRecordMapper;

    @Override
    public PageInfo<CompetitionPageResModel> page(CompetitionPageReqModel reqModel) {
        List<Long> competitionIds = new ArrayList<>();
        if (reqModel.getClassId() != null || reqModel.getStudentId() != null) {
            //班级/学生信息不为空，查询参与的比赛id
            competitionIds = competitionRecordMapper.partakeCompetitionList(reqModel);
            if (CollectionUtils.isEmpty(competitionIds)) {
                //未查询到有关的比赛直接返回空
                return new PageInfo<>(new ArrayList<>());
            }
        }
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<CompetitionPageResModel> list = this.getBaseMapper().page(competitionIds, reqModel);
        return new PageInfo<>(list);
    }

    @Override
    @Transactional
    public Long save(CompetitionSaveReqModel reqModel) {
        CompetitionEntity entity = BeanConvertUtil.convert(reqModel, CompetitionEntity.class);
        this.save(entity);
        return entity.getId();
    }

    @Override
    @Transactional
    public void updateCompetition(Long id, CompetitionSaveReqModel reqModel) {
        CompetitionEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.COMPETITION_NOT_EXIST);
        }
        BeanUtils.copyProperties(reqModel, entity);
        this.updateById(entity);
    }

    @Override
    @Transactional
    public void deleteCompetition(Long id) {
        CompetitionEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.COMPETITION_NOT_EXIST);
        }
        this.removeById(id);
    }

    @Override
    public CompetitionResModel getCompetitionById(Long id) {
        CompetitionEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.COMPETITION_NOT_EXIST);
        }
        return BeanConvertUtil.convert(entity, CompetitionResModel.class);
    }
}