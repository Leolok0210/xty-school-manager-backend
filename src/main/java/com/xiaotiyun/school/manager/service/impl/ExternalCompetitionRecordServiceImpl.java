package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.DateUtils;
import com.xiaotiyun.school.manager.dao.ExternalCompetitionRecordMapper;
import com.xiaotiyun.school.manager.model.dto.ExternalCompetitionRecordDTO;
import com.xiaotiyun.school.manager.model.dto.StudentCountDTO;
import com.xiaotiyun.school.manager.model.entity.ExternalCompetitionEntity;
import com.xiaotiyun.school.manager.model.entity.ExternalCompetitionGroupEntity;
import com.xiaotiyun.school.manager.model.entity.ExternalCompetitionRecordEntity;
import com.xiaotiyun.school.manager.model.entity.SemesterEntity;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionQueryReqModel;
import com.xiaotiyun.school.manager.model.res.TranScriptExternalCompetitionResModel;
import com.xiaotiyun.school.manager.model.res.TranScriptGenerateResModel;
import com.xiaotiyun.school.manager.service.ExternalCompetitionGroupService;
import com.xiaotiyun.school.manager.service.ExternalCompetitionRecordService;
import com.xiaotiyun.school.manager.service.ExternalCompetitionService;
import com.xiaotiyun.school.manager.service.SemesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExternalCompetitionRecordServiceImpl extends ServiceImpl<ExternalCompetitionRecordMapper, ExternalCompetitionRecordEntity>
        implements ExternalCompetitionRecordService {
    @Autowired
    private SemesterService semesterService;
    @Lazy
    @Autowired
    private ExternalCompetitionService externalCompetitionService;
    @Autowired
    private ExternalCompetitionGroupService externalCompetitionGroupService;

    @Override
    public List<Long> partakeCompetitionList(ExternalCompetitionQueryReqModel reqModel) {
        return this.getBaseMapper().partakeCompetitionList(reqModel);
    }

    @Override
    public Map<Long, Integer> countCompetitionRecords(Long semesterId, Long classId) {
        SemesterEntity semester = semesterService.getById(semesterId);


        LocalDateTime semesterStart = semester.getStartTime();
        LocalDateTime semesterEnd = semester.getEndTime();
        List<StudentCountDTO> studentCountDTOS = this.baseMapper.countRecordsByDateRange(semesterStart.toLocalDate(), semesterEnd.toLocalDate(), classId);
        return studentCountDTOS.stream().collect(Collectors.toMap(StudentCountDTO::getStudentId, StudentCountDTO::getCount)
        );
    }

    @Override
    public List<ExternalCompetitionRecordDTO> getCompetitionRecords(Long semesterId, Long classId) {
        SemesterEntity semester = semesterService.getById(semesterId);


        LocalDateTime semesterStart = semester.getStartTime();
        LocalDateTime semesterEnd = semester.getEndTime();

        return this.baseMapper.getCompetitionRecords(semesterStart, semesterEnd, classId);
    }

    @Override
    public void getByTranScriptGenerateResModel(List<TranScriptGenerateResModel> result) {
        List<Long> studentIds = result.stream().map(TranScriptGenerateResModel::getStudentId).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(studentIds)){
            return;
        }
        // 获取学生参与的比赛记录
        List<ExternalCompetitionRecordEntity> competitionRecords = this.list(Wrappers.<ExternalCompetitionRecordEntity>lambdaQuery()
                .in(ExternalCompetitionRecordEntity::getStudentId, studentIds));
        if(CollectionUtils.isEmpty(competitionRecords)){
            return;
        }
        // 获取比赛信息
        Set<Long> competitionIds = competitionRecords.stream().map(ExternalCompetitionRecordEntity::getCompetitionId).collect(Collectors.toSet());
        List<ExternalCompetitionEntity> externalCompetitionEntities = externalCompetitionService.listByIds(competitionIds);
        if(CollectionUtils.isEmpty(externalCompetitionEntities)){
            return;
        }
        Map<Long, ExternalCompetitionEntity> competitionMap = externalCompetitionEntities.stream().collect(Collectors.toMap(ExternalCompetitionEntity::getId, e -> e));
        // 获取组别信息
        Set<Long> groupIds = competitionRecords.stream().map(ExternalCompetitionRecordEntity::getGroupId).collect(Collectors.toSet());
        List<ExternalCompetitionGroupEntity> externalCompetitionGroupEntities = externalCompetitionGroupService.listByIds(groupIds);
        if(CollectionUtils.isEmpty(externalCompetitionGroupEntities)){
            return;
        }
        Map<Long, ExternalCompetitionGroupEntity> groupMap = externalCompetitionGroupEntities.stream().collect(Collectors.toMap(ExternalCompetitionGroupEntity::getId, e -> e));
        // 构建结果
        Map<Long, List<ExternalCompetitionRecordEntity>> recordsMap = competitionRecords.stream().collect(Collectors.groupingBy(ExternalCompetitionRecordEntity::getStudentId));
        for (TranScriptGenerateResModel student : result) {
            List<ExternalCompetitionRecordEntity> records = recordsMap.get(student.getStudentId());
            if(CollectionUtils.isEmpty(records)){
                continue;
            }
            List<TranScriptExternalCompetitionResModel> externalCompetitionResModels = records.stream().map(record -> {
                ExternalCompetitionEntity competition = competitionMap.get(record.getCompetitionId());
                ExternalCompetitionGroupEntity group = groupMap.get(record.getGroupId());
                TranScriptExternalCompetitionResModel resModel = new TranScriptExternalCompetitionResModel();
                resModel.setCompetitionName(competition.getName());
                resModel.setCompetitionType(competition.getCategoryName());
                resModel.setCompetitionGroupName(group.getGroupName());
                resModel.setAward(record.getAwardsName());
                resModel.setFinalAward(record.getFinalAwards());
                return resModel;
            }).collect(Collectors.toList());
            student.setExternalCompetitionRecords(externalCompetitionResModels);
        }
    }
}