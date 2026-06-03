package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.annotations.DataOperationLog;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.DataBusinessTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.DataOperationTypeEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.dao.CompetitionRecordMapper;
import com.xiaotiyun.school.manager.model.dto.CompetitionStudentCountDTO;
import com.xiaotiyun.school.manager.model.entity.CompetitionEntity;
import com.xiaotiyun.school.manager.model.entity.CompetitionRecordEntity;
import com.xiaotiyun.school.manager.model.entity.SemesterEntity;
import com.xiaotiyun.school.manager.model.req.CompetitionRecordBatchCreateReqModel;
import com.xiaotiyun.school.manager.model.req.CompetitionRecordPageReqModel;
import com.xiaotiyun.school.manager.model.req.CompetitionRecordStudentPageReqModel;
import com.xiaotiyun.school.manager.model.req.CompetitionRecordUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.CompetitionRecordResModel;
import com.xiaotiyun.school.manager.model.res.CompetitionStudentPageResModel;
import com.xiaotiyun.school.manager.service.CompetitionRecordService;
import com.xiaotiyun.school.manager.service.CompetitionService;
import com.xiaotiyun.school.manager.service.SemesterService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompetitionRecordServiceImpl extends ServiceImpl<CompetitionRecordMapper, CompetitionRecordEntity> implements CompetitionRecordService {
    private final CompetitionService competitionService;

    private final SemesterService semesterService;

    @Override
    @Transactional
    @DataOperationLog(opType = DataOperationTypeEnum.UPDATE, businessType = DataBusinessTypeEnum.EXTRA_CURRICULAR_COMPETITION)
    public CompetitionRecordEntity updateRecord(Long recordId, CompetitionRecordUpdateReqModel reqModel) {
        CompetitionRecordEntity entity = getById(recordId);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        // 使用BeanUtils替代BeanConvertUtil
        BeanUtils.copyProperties(reqModel, entity);
        this.updateById(entity);
        return entity;
    }

    @Override
    @Transactional
    @DataOperationLog(opType = DataOperationTypeEnum.CREATE, businessType = DataBusinessTypeEnum.EXTRA_CURRICULAR_COMPETITION)
    public List<CompetitionRecordEntity> batchCreateRecords(CompetitionRecordBatchCreateReqModel reqModel) {
        // 校验比赛存在性
        CompetitionEntity competition = competitionService.getById(reqModel.getCompetitionId());
        if (competition == null) {
            throw new BusinessException(LanguageConstants.COMPETITION_NOT_EXIST);
        }
        List<CompetitionRecordEntity> entities = reqModel.getRecords().stream()
                .map(record -> {
                    CompetitionRecordEntity entity = BeanConvertUtil.convert(record, CompetitionRecordEntity.class);
                    entity.setCompetitionId(reqModel.getCompetitionId());
                    return entity;
                })
                .collect(Collectors.toList());

        this.saveBatch(entities);
        return entities;
    }

    @Override
    public PageInfo<CompetitionRecordResModel> getRecordPage(CompetitionRecordPageReqModel reqModel) {
        // 设置分页参数，默认pageSize=10
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<CompetitionRecordResModel> list = baseMapper.selectRecordPage(reqModel);
        return new PageInfo<>(list);
    }

    @Override
    @Transactional
    public void deleteRecord(Long recordId) {
        CompetitionRecordEntity entity = getById(recordId);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        this.removeById(recordId);
    }

    @Override
    public PageInfo<CompetitionStudentPageResModel> studentPage(CompetitionRecordStudentPageReqModel reqModel) {
        // 设置分页参数，默认pageSize=10
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<CompetitionStudentPageResModel> list = this.baseMapper.selectStudentRecordPage(reqModel);
        return new PageInfo<>(list);
    }

    @Override
    public List<CompetitionStudentCountDTO> getCountStudent(Long classId, Long periodId) {
        // 查询学期时间
        SemesterEntity semester = semesterService.getById(periodId);

        LocalDateTime semesterStart = semester.getStartTime();
        LocalDateTime semesterEnd = semester.getEndTime();
        List<CompetitionStudentCountDTO> countStudent = this.baseMapper.getCountStudent(semesterStart, semesterEnd, classId);

        if (CollectionUtils.isEmpty(countStudent)) {
            return new ArrayList<>();
        }
        return countStudent;
    }
}