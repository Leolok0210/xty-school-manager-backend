package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.dao.SystemSettingDao;
import com.xiaotiyun.school.manager.dao.SystemSettingHistoryDao;
import com.xiaotiyun.school.manager.model.dto.SystemSettingHistoryDTO;
import com.xiaotiyun.school.manager.model.entity.SystemSettingEntity;
import com.xiaotiyun.school.manager.model.entity.SystemSettingHistoryEntity;
import com.xiaotiyun.school.manager.model.req.SystemSettingUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.SystemSettingResModel;
import com.xiaotiyun.school.manager.service.SystemSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SystemSettingServiceImpl extends ServiceImpl<SystemSettingDao, SystemSettingEntity> implements SystemSettingService {

    @Autowired
    private SystemSettingHistoryDao systemSettingHistoryDao;

    @Override
    public SystemSettingResModel getSchoolSettings(Long schoolId) {
        // 查询学校所有配置
        List<SystemSettingEntity> settings = this.list(new LambdaQueryWrapper<SystemSettingEntity>()
                .eq(SystemSettingEntity::getSchoolId, schoolId));
        
        // 转换为Map
        Map<String, String> settingMap = new HashMap<>();
        settings.forEach(setting -> settingMap.put(setting.getSettingKey(), setting.getSettingValue()));
        
        // 封装返回结果
        SystemSettingResModel resModel = new SystemSettingResModel();
        resModel.setSchoolId(schoolId);
        resModel.setSettings(settingMap);
        return resModel;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSettings(SystemSettingUpdateReqModel reqModel) {
        Long schoolId = reqModel.getSchoolId();
        
        // 遍历需要更新的配置
        reqModel.getSettings().forEach((key, newValue) -> {
            // 查询原配置
            SystemSettingEntity setting = this.getOne(new LambdaQueryWrapper<SystemSettingEntity>()
                    .eq(SystemSettingEntity::getSchoolId, schoolId)
                    .eq(SystemSettingEntity::getSettingKey, key));
            
            String oldValue = null;
            if (setting == null) {
                // 新增配置
                setting = new SystemSettingEntity();
                setting.setSchoolId(schoolId);
                setting.setSettingKey(key);
            } else {
                oldValue = setting.getSettingValue();
                // 值未变更,不需要更新
                if (oldValue.equals(newValue)) {
                    return;
                }
            }
            
            // 更新配置值
            setting.setSettingValue(newValue);
            setting.setUpdateTime(LocalDateTime.now());
            this.saveOrUpdate(setting);
            
            // 记录历史
            SystemSettingHistoryEntity history = new SystemSettingHistoryEntity();
            history.setSchoolId(schoolId);
            history.setSettingKey(key);
            history.setOldValue(oldValue);
            history.setNewValue(newValue);
            systemSettingHistoryDao.insert(history);
        });
    }

    @Override
    public List<SystemSettingHistoryDTO> getHistoryConfigsBySemesterTime(
            Long schoolId, 
            String settingKey, 
            LocalDateTime semesterStartTime, 
            LocalDateTime semesterEndTime
    ) {
        return systemSettingHistoryDao.getHistoryConfigsBySemesterTime(
                schoolId, settingKey, semesterStartTime, semesterEndTime);
    }
    
    @Override
    public SystemSettingEntity getLatestConfig(Long schoolId, String settingKey) {
        return this.getOne(new LambdaQueryWrapper<SystemSettingEntity>()
                .eq(SystemSettingEntity::getSchoolId, schoolId)
                .eq(SystemSettingEntity::getSettingKey, settingKey)
                .orderByDesc(SystemSettingEntity::getUpdateTime)
                .last("LIMIT 1"));
    }
}