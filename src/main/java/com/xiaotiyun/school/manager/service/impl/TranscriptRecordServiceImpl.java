package com.xiaotiyun.school.manager.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.DepartmentScoreRuleEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.dao.TranscriptRecordDao;
import com.xiaotiyun.school.manager.helper.UserAuthHelper;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.SubjectRelResModel;
import com.xiaotiyun.school.manager.model.res.TranscriptRecordResModel;
import com.xiaotiyun.school.manager.service.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TranscriptRecordServiceImpl extends ServiceImpl<TranscriptRecordDao, TranscriptRecordEntity> implements TranscriptRecordService {

    @Resource
    private SysClassService sysClassService;


    @Resource
    private SystemSettingService systemSettingService;

    @Resource
    private SysSemesterRuleService sysSemesterRuleService;
    @Resource
    private DepartmentScoreRuleService departmentScoreRuleService;
    @Resource
    private SubjectLevelRuleService subjectLevelRuleService;
    @Resource
    private StudentUsuallyRuleService studentUsuallyRuleService;

    @Resource
    private TranScriptDetailsService tranScriptDetailsService;


    @Resource
    private SubjectRelService subjectRelService;


    @Resource
    private UserAuthHelper userAuthHelper;


    @Resource
    private GradeGroupService gradeGroupService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(TranscriptRecordReqModel reqModel) {
        // 检查规则都有配置
        if (!checkRuleExists(reqModel)) {
            throw new BusinessException(LanguageConstants.SUBJECT_LEVEL_RULE_NOT_EXISTS);
        }
        // 若有班级ID
        if (ObjectUtils.isNotEmpty(reqModel.getClassId())){
            if (count(Wrappers.<TranscriptRecordEntity>lambdaQuery().in(TranscriptRecordEntity::getClassId, reqModel.getClassId())) > 0)
                return ;
            addEntity(reqModel.getClassId());
            return ;
        }
        // 若有级组ID
        if (ObjectUtils.isNotEmpty(reqModel.getGradeGroup())){
            List<TranscriptRecordEntity> list = list(Wrappers.<TranscriptRecordEntity>lambdaQuery()
                    .in(TranscriptRecordEntity::getGradeGroup, reqModel.getGradeGroup()));
            List<SysClass> classList = sysClassService.list(Wrappers.<SysClass>lambdaQuery()
                    .in(SysClass::getGradeGroup, reqModel.getGradeGroup()));
            addEntity(getNotRepeat(classList, list));
            return ;
        }
        // 若有学部
        if (ObjectUtils.isNotEmpty(reqModel.getDepartment())){
            List<TranscriptRecordEntity> list = list(Wrappers.<TranscriptRecordEntity>lambdaQuery()
                    .in(TranscriptRecordEntity::getDepartment, reqModel.getDepartment()));
            List<SysClass> classList = sysClassService.list(Wrappers.<SysClass>lambdaQuery()
                    .in(SysClass::getDepartment, reqModel.getDepartment()));
            addEntity(getNotRepeat(classList, list));
            return ;
        }
        // 只有学年
        List<TranscriptRecordEntity> list = list(Wrappers.<TranscriptRecordEntity>lambdaQuery()
                .eq(TranscriptRecordEntity::getSchoolYear, reqModel.getSchoolYear()));
        List<SysClass> classList = sysClassService.list(Wrappers.<SysClass>lambdaQuery()
                .eq(SysClass::getSid, reqModel.getSchoolYear()));
        addEntity(getNotRepeat(classList, list));
    }

    private boolean checkRuleExists(TranscriptRecordReqModel reqModel) {
        //获取这个学校下面的全部级组
        LambdaQueryWrapper<GradeGroup> reqGradeGroup = new LambdaQueryWrapper<>();
        reqGradeGroup.eq(GradeGroup::getSchoolId, reqModel.getSchoolId());
        if(!CollectionUtils.isEmpty(reqModel.getDepartment()))
        {
            reqGradeGroup.in(GradeGroup::getDepartment, reqModel.getDepartment());
        }
        if(!CollectionUtils.isEmpty(reqModel.getGradeGroup()))
        {
            reqGradeGroup.in(GradeGroup::getId, reqModel.getGradeGroup());
        }
        List<GradeGroup> gradeAllGroupList = gradeGroupService.list(reqGradeGroup);
        if (ObjectUtils.isEmpty(gradeAllGroupList)) {
            return false;
        }
        for (GradeGroup gradeGroup : gradeAllGroupList)
        {
            // 获取科目
            SubjectRelGroupQueryReqModel reqSubjectRel = new SubjectRelGroupQueryReqModel();
            reqSubjectRel.setSchoolId(reqModel.getSchoolId());
            reqSubjectRel.setCountedInAverage(1);
            reqSubjectRel.setGroupId(gradeGroup.getId());
            List<SubjectRelResModel> relResModels = subjectRelService.listByGroup(reqSubjectRel);
            if (ObjectUtils.isEmpty(relResModels)) {
                return false;
            }
            List<DepartmentScoreRuleEntity> scoreRuleEntities = departmentScoreRuleService.list(Wrappers.<DepartmentScoreRuleEntity>lambdaQuery()
                    .eq(DepartmentScoreRuleEntity::getSchoolId, reqModel.getSchoolId())
                    .eq(DepartmentScoreRuleEntity::getGroupId, gradeGroup.getId()));

            if (ObjectUtils.isEmpty(scoreRuleEntities)) {
                return false;
            }
        }
        return sysSemesterRuleService.count(Wrappers.<SysSemesterRuleEntity>lambdaQuery()//学段权重配置
                .eq(SysSemesterRuleEntity::getSchoolId, reqModel.getSchoolId())
                .eq(SysSemesterRuleEntity::getSchoolYear, reqModel.getSchoolYear())) != 0
                &&
                studentUsuallyRuleService.count(Wrappers.<StudentUsuallyRuleEntity>lambdaQuery()//平时粉权重配置
                        .eq(StudentUsuallyRuleEntity::getSchoolId, reqModel.getSchoolId())) != 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, TranscriptRecordReqModel reqModel) {
        // 若有班级ID
        if (ObjectUtils.isNotEmpty(reqModel.getClassId())){
            if(count(Wrappers.<TranscriptRecordEntity>lambdaQuery()
                            .in(TranscriptRecordEntity::getClassId, reqModel.getClassId())) == 0){
                throw new BusinessException(LanguageConstants.EXAM_SCORE_NOT_EXISTS_NO_SCORE);
            }
            update(Wrappers.<TranscriptRecordEntity>lambdaUpdate()
                    .in(TranscriptRecordEntity::getClassId, reqModel.getClassId())
                    .set(TranscriptRecordEntity::getStatus, 0));
            return ;
        }
        // 若有级组ID
        if (ObjectUtils.isNotEmpty(reqModel.getGradeGroup())){
            if(count(Wrappers.<TranscriptRecordEntity>lambdaQuery()
                            .in(TranscriptRecordEntity::getGradeGroup, reqModel.getGradeGroup())) == 0){
                throw new BusinessException(LanguageConstants.EXAM_SCORE_NOT_EXISTS_NO_SCORE);
            }
            update(Wrappers.<TranscriptRecordEntity>lambdaUpdate()
                    .in(TranscriptRecordEntity::getGradeGroup, reqModel.getGradeGroup())
                    .set(TranscriptRecordEntity::getStatus, 0));
            return ;
        }
        // 若有学部
        if (ObjectUtils.isNotEmpty(reqModel.getDepartment())){
            if(count(Wrappers.<TranscriptRecordEntity>lambdaQuery()
                            .in(TranscriptRecordEntity::getDepartment, reqModel.getDepartment())) == 0){
                throw new BusinessException(LanguageConstants.EXAM_SCORE_NOT_EXISTS_NO_SCORE);
            }
            update(Wrappers.<TranscriptRecordEntity>lambdaUpdate()
                    .in(TranscriptRecordEntity::getDepartment, reqModel.getDepartment())
                    .set(TranscriptRecordEntity::getStatus, 0));
            return ;
        }
        // 学年
        if(count(Wrappers.<TranscriptRecordEntity>lambdaQuery()
                .eq(TranscriptRecordEntity::getSchoolYear, reqModel.getSchoolYear())) == 0){
            throw new BusinessException(LanguageConstants.EXAM_SCORE_NOT_EXISTS_NO_SCORE);
        }
        update(Wrappers.<TranscriptRecordEntity>lambdaUpdate()
                .in(TranscriptRecordEntity::getSchoolYear, reqModel.getSchoolYear())
                .set(TranscriptRecordEntity::getStatus, 0));
    }

    @Override
    public void updateStatusAndZipUrl(TranscriptRecordUpdateReq reqModel) {
        update(Wrappers.<TranscriptRecordEntity>lambdaUpdate()
                .eq(BaseEntity::getId, reqModel.getId())
                .set(TranscriptRecordEntity::getStatus, reqModel.getStatus())
                .set(TranscriptRecordEntity::getZipUrl, reqModel.getZipUrl()));
    }

    @Override
    public PageInfo<TranscriptRecordResModel> page(TranscriptRecordQueryReqModel reqModel) {
        boolean commonUser = userAuthHelper.getCommonUser(reqModel.getUserId(), reqModel.getSchoolId());
        List<Long> classIds = null;
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(reqModel.getUserId(), reqModel.getSchoolId());
            if(CollectionUtils.isEmpty(classIds))
            {
                return new PageInfo<>(new ArrayList<>());
            }
            reqModel.setClassIds(classIds);
        }
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<TranscriptRecordResModel> list = this.getBaseMapper().page(reqModel);

        return new PageInfo<>(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        TranscriptRecordEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        if (entity.getStatus() == 0) {
            throw new BusinessException(LanguageConstants.PROCESSING_CANNOT_DELETE);
        }
        boolean result = removeById(id);
        if(result){
            // 如果删除成功，需要删除全班成绩单
            tranScriptDetailsService.remove(Wrappers.<TranscriptDetailsEntity>lambdaQuery()
                    .eq(TranscriptDetailsEntity::getClassId, entity.getClassId()));
        }
    }

    private List<Long> getNotRepeat(List<SysClass> classList, List<TranscriptRecordEntity> list) {
        if (ObjectUtils.isNotEmpty(list)) {
            List<Long> oldCLassList = list.stream().map(TranscriptRecordEntity::getClassId).collect(Collectors.toList());
            return classList.stream().map(BaseEntity::getId).filter(id -> !oldCLassList.contains(id)).collect(Collectors.toList());
        } else {
            return classList.stream().map(BaseEntity::getId).collect(Collectors.toList());
        }
    }

    private void addEntity(List<Long> longs) {
        if (ObjectUtils.isEmpty(longs)) {
            return;
        }
        UserEntity userInfo = (UserEntity) StpUtil.getSession().get("userInfo");
        if (userInfo == null) {
            throw new BusinessException(LanguageConstants.SYSTEM_TOKEN_INVALID);
        }
        List<SysClass> sysClasses = sysClassService.listByIds(longs);
        List<TranscriptRecordEntity> entities = new ArrayList<>();
        sysClasses.forEach(sysClass -> {
            TranscriptRecordEntity entity = new TranscriptRecordEntity();
            BeanUtils.copyProperties(sysClass, entity);
            entity.setId(null);
            entity.setSchoolYear(sysClass.getSid());
            entity.setClassId(sysClass.getId());
            entity.setStatus(0);
            entity.setRegistrant(userInfo.getUsername());
            entity.setRegistrantId(userInfo.getId());
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            entities.add(entity);
        });
        saveBatch(entities);
    }
}