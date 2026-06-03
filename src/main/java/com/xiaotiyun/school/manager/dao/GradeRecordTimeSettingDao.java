package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.GradeRecordTimeSettingEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface GradeRecordTimeSettingDao extends BaseMapper<GradeRecordTimeSettingEntity> {

    /**
     * 查询有效的时间设置（关联学段表过滤已删除学段）
     *
     * @param schoolId 学校ID
     * @param schoolYear 学年
     * @return 时间设置列表
     */
    @Select("SELECT t.* FROM grade_record_time_setting t " +
            "INNER JOIN sys_semester s ON t.semester_id = s.id " +
            "WHERE t.school_id = #{schoolId} " +
            "AND t.school_year = #{schoolYear} " +
            "AND t.deleted = 0 " +
            "AND s.deleted = 0 " +
            "ORDER BY t.department, t.semester_id")
    List<GradeRecordTimeSettingEntity> selectValidTimeSettings(@Param("schoolId") Long schoolId, 
                                                              @Param("schoolYear") String schoolYear);
} 