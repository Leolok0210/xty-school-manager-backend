package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.dao.GradeRecordTimeSettingDao;
import com.xiaotiyun.school.manager.dao.GradeRecordClassSettingDao;
import com.xiaotiyun.school.manager.model.entity.GradeRecordTimeSettingEntity;
import com.xiaotiyun.school.manager.model.entity.GradeRecordClassSettingEntity;
import com.xiaotiyun.school.manager.model.req.GradeRecordSettingSaveReqModel;
import com.xiaotiyun.school.manager.service.GradeGroupService;
import com.xiaotiyun.school.manager.service.GradeRecordSettingService;
import com.xiaotiyun.school.manager.service.SemesterService;
import com.xiaotiyun.school.manager.service.SysClassService;
import com.xiaotiyun.school.manager.model.res.GradeRecordSettingResModel;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@Service
public class GradeRecordSettingServiceImpl implements GradeRecordSettingService {

    @Resource
    private GradeRecordTimeSettingDao timeSettingDao;
    
    @Resource
    private GradeRecordClassSettingDao classSettingDao;

    @Resource
    private SemesterService semesterService;

    @Resource
    private SysClassService sysClassService;

    @Resource
    private GradeGroupService gradeGroupService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSetting(Long schoolId, GradeRecordSettingSaveReqModel reqModel) {
        // 处理时间设定
        if (reqModel.getTimeSettings() != null && !reqModel.getTimeSettings().isEmpty()) {
            for (GradeRecordSettingSaveReqModel.TimeSettingItem item : reqModel.getTimeSettings()) {
                // 如果开始时间和结束时间都为空,则跳过
                if (item.getStartTime() == null && item.getEndTime() == null) {
                    continue;
                }
                
                // 校验开始时间和结束时间
                if ((item.getStartTime() != null && item.getEndTime() == null) || 
                    (item.getStartTime() == null && item.getEndTime() != null)) {
                    throw new BusinessException(LanguageConstants.TIME_SETTING_BOTH_REQUIRED);
                }
                
                if (item.getStartTime() != null && item.getEndTime() != null && 
                    item.getStartTime().isAfter(item.getEndTime())) {
                    throw new BusinessException(LanguageConstants.TIME_SETTING_START_AFTER_END);
                }

                // 查找是否已存在设置
                GradeRecordTimeSettingEntity entity = timeSettingDao.selectOne(
                    new LambdaQueryWrapper<GradeRecordTimeSettingEntity>()
                        .eq(GradeRecordTimeSettingEntity::getSchoolId, schoolId)
                        .eq(GradeRecordTimeSettingEntity::getSchoolYear, reqModel.getSchoolYear())
                        .eq(GradeRecordTimeSettingEntity::getSemesterId, item.getSemesterId())
                        .eq(GradeRecordTimeSettingEntity::getDepartment, item.getDepartment())
                        .eq(GradeRecordTimeSettingEntity::getDeleted, 0));
                
                if (entity == null) {
                    // 新增
                    entity = new GradeRecordTimeSettingEntity();
                    entity.setSchoolId(schoolId);
                    entity.setSchoolYear(reqModel.getSchoolYear());
                    entity.setSemesterId(item.getSemesterId());
                    entity.setDepartment(item.getDepartment());
                    entity.setStartTime(item.getStartTime());
                    entity.setEndTime(item.getEndTime());
                    timeSettingDao.insert(entity);
                } else {
                    // 更新
                    entity.setStartTime(item.getStartTime());
                    entity.setEndTime(item.getEndTime());
                    timeSettingDao.updateById(entity);
                }
            }
        }
        
        // 处理班级设定
        if (reqModel.getClassSettings() != null && !reqModel.getClassSettings().isEmpty()) {
            for (GradeRecordSettingSaveReqModel.ClassSettingItem setting : reqModel.getClassSettings()) {
                // 查找是否已存在设置
                GradeRecordClassSettingEntity entity = classSettingDao.selectOne(
                    new LambdaQueryWrapper<GradeRecordClassSettingEntity>()
                        .eq(GradeRecordClassSettingEntity::getSchoolId, schoolId)
                        .eq(GradeRecordClassSettingEntity::getSchoolYear, reqModel.getSchoolYear())
                        .eq(GradeRecordClassSettingEntity::getClassId, setting.getClassId())
                        .eq(GradeRecordClassSettingEntity::getDeleted, 0));
                
                if (entity == null) {
                    // 新增
                    entity = new GradeRecordClassSettingEntity();
                    entity.setSchoolId(schoolId);
                    entity.setSchoolYear(reqModel.getSchoolYear());
                    entity.setClassId(setting.getClassId());
                    entity.setGradeId(setting.getGradeId());
                    entity.setCanRecordExam(setting.getCanRecordExam());
                    entity.setCanRecordGraduation(setting.getCanRecordGraduation());
                    entity.setCanRecordMoralEducation(setting.getCanRecordMoralEducation());
                    entity.setCanRecordVolunteer(setting.getCanRecordVolunteer());
                    entity.setCanRecordConduct(setting.getCanRecordConduct());
                    classSettingDao.insert(entity);
                } else {
                    // 更新
                    entity.setCanRecordExam(setting.getCanRecordExam());
                    entity.setCanRecordGraduation(setting.getCanRecordGraduation());
                    entity.setCanRecordMoralEducation(setting.getCanRecordMoralEducation());
                    entity.setCanRecordVolunteer(setting.getCanRecordVolunteer());
                    entity.setCanRecordConduct(setting.getCanRecordConduct());
                    classSettingDao.updateById(entity);
                }
            }
        }
    }

    public List<GradeRecordTimeSettingEntity> listTimeSettings(Long schoolId, String schoolYear) {
        // 使用 timeSettingDao 自定义方法查询有效的时间设置
        return timeSettingDao.selectValidTimeSettings(schoolId, schoolYear);
    }

    public List<GradeRecordClassSettingEntity> listClassSettings(Long schoolId, String schoolYear) {
        return classSettingDao.selectValidClassSettings(schoolId, schoolYear);
    }

    @Override
    public GradeRecordSettingResModel getSetting(Long schoolId, String schoolYear) {
        GradeRecordSettingResModel resModel = new GradeRecordSettingResModel();
        resModel.setSchoolYear(schoolYear);
        
        // 查询时间设定
        List<GradeRecordTimeSettingEntity> timeSettings = listTimeSettings(schoolId, schoolYear);
        if (!timeSettings.isEmpty()) {
            // 批量获取学段名称
            List<Long> semesterIds = timeSettings.stream()
                    .map(GradeRecordTimeSettingEntity::getSemesterId)
                    .distinct()
                    .collect(Collectors.toList());
            Map<Long, String> semesterNameMap = semesterService.getNamesByIds(semesterIds);
            
            resModel.setTimeSettings(timeSettings.stream().map(entity -> {
                GradeRecordSettingResModel.TimeSettingItem item = new GradeRecordSettingResModel.TimeSettingItem();
                item.setId(entity.getId());
                item.setSemesterId(entity.getSemesterId());
                item.setSemesterName(semesterNameMap.get(entity.getSemesterId()));
                item.setDepartment(entity.getDepartment());
                item.setStartTime(entity.getStartTime());
                item.setEndTime(entity.getEndTime());
                return item;
            }).collect(Collectors.toList()));
        }
        
        // 查询班级设定
        List<GradeRecordClassSettingEntity> classSettings = listClassSettings(schoolId, schoolYear);
        if (!classSettings.isEmpty()) {
            // 批量获取班级名称
            List<Long> classIds = classSettings.stream()
                    .map(GradeRecordClassSettingEntity::getClassId)
                    .distinct()
                    .collect(Collectors.toList());
            Map<Long, String> classNameMap = sysClassService.getNamesByIds(classIds);

            // 批量获取级组名称
            Map<Long, String> gradeNameMap = gradeGroupService.getNamesByIds(classIds);
            
            resModel.setClassSettings(classSettings.stream().map(entity -> {
                GradeRecordSettingResModel.ClassSettingItem item = new GradeRecordSettingResModel.ClassSettingItem();
                item.setClassId(entity.getClassId());
                item.setGradeName(gradeNameMap.get(entity.getGradeId()));
                item.setGradeId(entity.getGradeId());
                item.setClassName(classNameMap.get(entity.getClassId()));
                item.setCanRecordExam(entity.getCanRecordExam());
                item.setCanRecordGraduation(entity.getCanRecordGraduation());
                item.setCanRecordMoralEducation(entity.getCanRecordMoralEducation());
                item.setCanRecordVolunteer(entity.getCanRecordVolunteer());
                item.setCanRecordConduct(entity.getCanRecordConduct());
                return item;
            }).collect(Collectors.toList()));
        }
        
        return resModel;
    }
} 