package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.FileRelevanceTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.FileTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.basic.util.DateUtils;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.TeacherBusinessDao;
import com.xiaotiyun.school.manager.dao.UserSchoolRelDao;
import com.xiaotiyun.school.manager.handler.ExportFileHandler;
import com.xiaotiyun.school.manager.model.entity.SysFileRelevanceEntity;
import com.xiaotiyun.school.manager.model.entity.TeacherBusinessEntity;
import com.xiaotiyun.school.manager.model.entity.UserSchoolRelEntity;
import com.xiaotiyun.school.manager.model.excel.TeacherBusinessExportEnModel;
import com.xiaotiyun.school.manager.model.excel.TeacherBusinessExportModel;
import com.xiaotiyun.school.manager.model.excel.TeacherBusinessExportPtModel;
import com.xiaotiyun.school.manager.model.req.ActApprovalInstancePreviewReqModel;
import com.xiaotiyun.school.manager.model.req.TeacherBusinessPageReqModel;
import com.xiaotiyun.school.manager.model.req.TeacherBusinessSaveReqModel;
import com.xiaotiyun.school.manager.model.req.TeacherBusinessStartReqModel;
import com.xiaotiyun.school.manager.model.res.TeacherBusinessPageResModel;
import com.xiaotiyun.school.manager.service.ActApprovalInstanceService;
import com.xiaotiyun.school.manager.service.SysFileRelevanceService;
import com.xiaotiyun.school.manager.service.TeacherBusinessService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherBusinessServiceImpl extends ServiceImpl<TeacherBusinessDao, TeacherBusinessEntity> implements TeacherBusinessService {
    private final UserSchoolRelDao userSchoolRelDao;
    private final ExportFileHandler exportFileHandler;
    private final SysFileRelevanceService sysFileRelevanceService;
    private final ActApprovalInstanceService actApprovalInstanceService;

    @Override
    public PageInfo<TeacherBusinessPageResModel> page(TeacherBusinessPageReqModel reqModel) {
        LocalDateTime queryStartTime = reqModel.getStartDate() != null ? reqModel.getStartDate().atStartOfDay() : null;
        LocalDateTime queryEndTime = reqModel.getEndDate() != null ? reqModel.getEndDate().atTime(LocalTime.MAX).withNano(0) : null;
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        QueryWrapper<TeacherBusinessEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(TeacherBusinessEntity::getSchoolId, reqModel.getSchoolId())
                .eq(reqModel.getTeacherId() != null && reqModel.getTeacherId() > 0, TeacherBusinessEntity::getTeacherId, reqModel.getTeacherId())
                .ge(queryStartTime != null, TeacherBusinessEntity::getEndTime, queryStartTime)
                .le(queryEndTime != null, TeacherBusinessEntity::getStartTime, queryEndTime)
                .eq(TeacherBusinessEntity::getDeleted, 0);
        List<TeacherBusinessEntity> list = this.list(wrapper.lambda().orderByDesc(TeacherBusinessEntity::getCreateTime));
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> userIds = list.stream().map(TeacherBusinessEntity::getTeacherId).collect(Collectors.toList());
            Map<Long, UserSchoolRelEntity> userMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(userIds)) {
                List<UserSchoolRelEntity> userSchoolRelEntities = userSchoolRelDao.selectList(new LambdaQueryWrapper<UserSchoolRelEntity>()
                        .eq(UserSchoolRelEntity::getSchoolId, reqModel.getSchoolId())
                        .in(UserSchoolRelEntity::getUserId, userIds)
                        .eq(UserSchoolRelEntity::getDeleted, false));
                if (CollectionUtils.isNotEmpty(userSchoolRelEntities)) {
                    userMap = userSchoolRelEntities.stream().collect(Collectors.toMap(UserSchoolRelEntity::getUserId, userSchoolRelEntity -> userSchoolRelEntity));
                }
            }
            PageInfo<TeacherBusinessEntity> pageInfo = new PageInfo<>(list);
            List<TeacherBusinessPageResModel> resList = new ArrayList<>();
            for (TeacherBusinessEntity entity : list) {
                TeacherBusinessPageResModel resModel = new TeacherBusinessPageResModel();
                BeanUtils.copyProperties(entity, resModel);
                UserSchoolRelEntity userSchoolRelEntity = userMap.get(entity.getTeacherId());
                if (userSchoolRelEntity != null) {
                    resModel.setTeacherName(userSchoolRelEntity.getUsername());
                }
                resList.add(resModel);
            }
            PageInfo<TeacherBusinessPageResModel> result = new PageInfo<>(resList);
            result.setTotal(pageInfo.getTotal());
            result.setPages(pageInfo.getPages());
            return result;
        }
        return null;
    }

    @Override
    @Transactional
    public void save(TeacherBusinessSaveReqModel reqModel) {
        if (!DateUtils.isTimeValid(reqModel.getStartTime(), reqModel.getEndTime())) {
            throw new BusinessException(LanguageConstants.START_TIME_AFTER_END_TIME);
        }
        checkDuplicate(null, reqModel);
        TeacherBusinessEntity entity = BeanConvertUtil.convert(reqModel, TeacherBusinessEntity.class);
        this.save(entity);
    }

    @Override
    @Transactional
    public void update(Long id, TeacherBusinessSaveReqModel reqModel) {
        if (!DateUtils.isTimeValid(reqModel.getStartTime(), reqModel.getEndTime())) {
            throw new BusinessException(LanguageConstants.START_TIME_AFTER_END_TIME);
        }
        checkDuplicate(id, reqModel);
        TeacherBusinessEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        BeanUtils.copyProperties(reqModel, entity);
        this.updateById(entity);
    }

    /**
     * 校验是否重复
     */
    private void checkDuplicate(Long id, TeacherBusinessSaveReqModel reqModel) {
        QueryWrapper<TeacherBusinessEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(TeacherBusinessEntity::getSchoolId, reqModel.getSchoolId())
                .eq(TeacherBusinessEntity::getTeacherId, reqModel.getTeacherId())
                .le(TeacherBusinessEntity::getStartTime, reqModel.getEndTime())
                .ge(TeacherBusinessEntity::getEndTime, reqModel.getStartTime())
                .eq(TeacherBusinessEntity::getDeleted, 0);
        if (id != null) {
            wrapper.lambda().ne(TeacherBusinessEntity::getId, id);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessException(LanguageConstants.OFFICIAL_RECORD_EXISTS);
        }
    }

    /**
     * 校验是否重复
     */
    private void checkDuplicate(Long id, Long schoolId, Long userId, TeacherBusinessStartReqModel reqModel) {
        QueryWrapper<TeacherBusinessEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(TeacherBusinessEntity::getSchoolId, schoolId)
                .eq(TeacherBusinessEntity::getTeacherId, userId)
                .le(TeacherBusinessEntity::getStartTime, reqModel.getEndTime())
                .ge(TeacherBusinessEntity::getEndTime, reqModel.getStartTime())
                .eq(TeacherBusinessEntity::getDeleted, 0);
        if (id != null) {
            wrapper.lambda().ne(TeacherBusinessEntity::getId, id);
        }
        if (this.count(wrapper) > 0) {
            throw new BusinessException(LanguageConstants.OFFICIAL_RECORD_EXISTS);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        TeacherBusinessEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        this.removeById(id);
    }

    @Override
    public String export(TeacherBusinessPageReqModel reqModel) {
        LocalDateTime queryStartTime = reqModel.getStartDate() != null ? reqModel.getStartDate().atStartOfDay() : null;
        LocalDateTime queryEndTime = reqModel.getEndDate() != null ? reqModel.getEndDate().atTime(LocalTime.MAX).withNano(0) : null;
        QueryWrapper<TeacherBusinessEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(TeacherBusinessEntity::getSchoolId, reqModel.getSchoolId())
                .eq(reqModel.getTeacherId() != null && reqModel.getTeacherId() > 0, TeacherBusinessEntity::getTeacherId, reqModel.getTeacherId())
                .ge(queryStartTime != null, TeacherBusinessEntity::getEndTime, queryStartTime)
                .le(queryEndTime != null, TeacherBusinessEntity::getStartTime, queryEndTime)
                .eq(TeacherBusinessEntity::getDeleted, 0);
        List<TeacherBusinessEntity> list = this.list(wrapper.lambda().orderByDesc(TeacherBusinessEntity::getCreateTime));
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> userIds = list.stream().map(TeacherBusinessEntity::getTeacherId).collect(Collectors.toList());
            Map<Long, UserSchoolRelEntity> userMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(userIds)) {
                List<UserSchoolRelEntity> userSchoolRelEntities = userSchoolRelDao.selectList(new LambdaQueryWrapper<UserSchoolRelEntity>()
                        .eq(UserSchoolRelEntity::getSchoolId, reqModel.getSchoolId())
                        .in(UserSchoolRelEntity::getUserId, userIds)
                        .eq(UserSchoolRelEntity::getDeleted, false));
                if (CollectionUtils.isNotEmpty(userSchoolRelEntities)) {
                    userMap = userSchoolRelEntities.stream().collect(Collectors.toMap(UserSchoolRelEntity::getUserId, userSchoolRelEntity -> userSchoolRelEntity));
                }
            }
            List<TeacherBusinessPageResModel> resList = new ArrayList<>();
            for (TeacherBusinessEntity entity : list) {
                TeacherBusinessPageResModel resModel = new TeacherBusinessPageResModel();
                BeanUtils.copyProperties(entity, resModel);
                UserSchoolRelEntity userSchoolRelEntity = userMap.get(entity.getTeacherId());
                if (userSchoolRelEntity != null) {
                    resModel.setTeacherName(userSchoolRelEntity.getUsername());
                }
                resList.add(resModel);
            }
            String fileName = "教师公务数据.xlsx";
            String currentLanguage = LanguageUtil.getCurrentLanguage();

            if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
                fileName = "Teacher Official Duties Data.xlsx";
                List<TeacherBusinessExportEnModel> exportEnModels = resList.stream()
                        .map(resModel -> {
                            TeacherBusinessExportEnModel exportModel = new TeacherBusinessExportEnModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setStartTime(resModel.getStartTime().toString());
                            exportModel.setEndTime(resModel.getEndTime().toString());
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportEnModels, fileName, TeacherBusinessExportEnModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());
            } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
                fileName = "Dados de Funções Oficiais do Professor.xlsx";
                List<TeacherBusinessExportPtModel> exportPtModels = resList.stream()
                        .map(resModel -> {
                            TeacherBusinessExportPtModel exportModel = new TeacherBusinessExportPtModel();
                            BeanUtils.copyProperties(resModel, exportModel);
                            exportModel.setStartTime(resModel.getStartTime().toString());
                            exportModel.setEndTime(resModel.getEndTime().toString());
                            return exportModel;
                        }).collect(Collectors.toList());
                return exportFileHandler.doExportExcel(exportPtModels, fileName, TeacherBusinessExportPtModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());
            } else {
                return exportFileHandler.doExportExcel(handleExportData(resList), fileName, TeacherBusinessExportModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());
            }
        }
        return null;
    }

    private List<TeacherBusinessExportModel> handleExportData(List<TeacherBusinessPageResModel> exportDTOS) {
        List<TeacherBusinessExportModel> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(exportDTOS)) {
            exportDTOS.forEach(resModel -> {
                TeacherBusinessExportModel exportModel = new TeacherBusinessExportModel();
                BeanUtils.copyProperties(resModel, exportModel);
                exportModel.setStartTime(resModel.getStartTime().toString());
                exportModel.setEndTime(resModel.getEndTime().toString());
                result.add(exportModel);
            });
        }
        return result;
    }

    @Override
    @Transactional
    public void start(Long schoolId, Long userId, TeacherBusinessStartReqModel reqModel) {
        if (!DateUtils.isTimeValid(reqModel.getStartTime(), reqModel.getEndTime())) {
            throw new BusinessException(LanguageConstants.START_TIME_AFTER_END_TIME);
        }
        checkDuplicate(null, schoolId, userId, reqModel);
        TeacherBusinessEntity entity = BeanConvertUtil.convert(reqModel, TeacherBusinessEntity.class);
        entity.setSchoolId(schoolId);
        entity.setTeacherId(userId);
        this.save(entity);
        // 关联所有文件
        if (CollectionUtils.isNotEmpty(reqModel.getFileIds())) {
            sysFileRelevanceService.saveBatch(reqModel.getFileIds().stream().map(fileId -> {
                SysFileRelevanceEntity fileRelevanceEntity = new SysFileRelevanceEntity();
                fileRelevanceEntity.setFileId(fileId);
                fileRelevanceEntity.setType(FileRelevanceTypeEnum.TEACHER_BUSINESS.getType());
                fileRelevanceEntity.setBusinessId(entity.getId());
                fileRelevanceEntity.setSchoolId(schoolId);
                return fileRelevanceEntity;
            }).collect(Collectors.toList()));
        }
        //发起请假流程
        ActApprovalInstancePreviewReqModel previewReqModel = new ActApprovalInstancePreviewReqModel();
        previewReqModel.setTemplateId(reqModel.getTemplateId());
        previewReqModel.setDefinitionId(reqModel.getDefinitionId());
        previewReqModel.setApplyDays(reqModel.getBusinessDays());
        previewReqModel.setApprover(reqModel.getApprover());
        actApprovalInstanceService.startProcess(schoolId, userId, entity.getId(), previewReqModel);
    }
}