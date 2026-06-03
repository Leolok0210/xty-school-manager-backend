package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.dto.SystemSettingHistoryDTO;
import com.xiaotiyun.school.manager.model.entity.SystemSettingHistoryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SystemSettingHistoryDao extends BaseMapper<SystemSettingHistoryEntity> {
    
    /**
     * 根据学期时间查询历史配置
     * 使用CTE查询获取与学期时间有交集的历史配置
     * 
     * @param schoolId 学校ID
     * @param settingKey 配置键名
     * @param semesterStartTime 学期开始时间
     * @param semesterEndTime 学期结束时间
     * @return 历史配置列表
     */
    List<SystemSettingHistoryDTO> getHistoryConfigsBySemesterTime(
            @Param("schoolId") Long schoolId,
            @Param("settingKey") String settingKey,
            @Param("semesterStartTime") LocalDateTime semesterStartTime,
            @Param("semesterEndTime") LocalDateTime semesterEndTime
    );
} 