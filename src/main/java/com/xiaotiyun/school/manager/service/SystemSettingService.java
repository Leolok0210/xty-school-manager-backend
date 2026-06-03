package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.dto.SystemSettingHistoryDTO;
import com.xiaotiyun.school.manager.model.entity.SystemSettingEntity;
import com.xiaotiyun.school.manager.model.req.SystemSettingUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.SystemSettingResModel;

import java.time.LocalDateTime;
import java.util.List;

public interface SystemSettingService extends IService<SystemSettingEntity> {
    
    /**
     * 获取学校配置
     */
    SystemSettingResModel getSchoolSettings(Long schoolId);
    
    /**
     * 更新学校配置
     */
    void updateSettings(SystemSettingUpdateReqModel reqModel);
    
    /**
     * 根据学期时间查询历史配置
     * 
     * @param schoolId 学校ID
     * @param settingKey 配置键名
     * @param semesterStartTime 学期开始时间
     * @param semesterEndTime 学期结束时间
     * @return 历史配置列表
     */
    List<SystemSettingHistoryDTO> getHistoryConfigsBySemesterTime(
            Long schoolId, 
            String settingKey, 
            LocalDateTime semesterStartTime, 
            LocalDateTime semesterEndTime
    );
    
    /**
     * 获取最新的配置
     * 
     * @param schoolId 学校ID
     * @param settingKey 配置键名
     * @return 最新配置
     */
    SystemSettingEntity getLatestConfig(Long schoolId, String settingKey);
}