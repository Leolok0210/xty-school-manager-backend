package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.GradeRecordClassSettingEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GradeRecordClassSettingDao extends BaseMapper<GradeRecordClassSettingEntity> {

    /**
     * 查询有效的班级成绩记录设置
     * @param schoolId 学校ID
     * @param schoolYear 学年
     * @return 班级成绩记录设置列表
     */
    List<GradeRecordClassSettingEntity> selectValidClassSettings(@Param("schoolId") Long schoolId, 
                                                                @Param("schoolYear") String schoolYear);
} 