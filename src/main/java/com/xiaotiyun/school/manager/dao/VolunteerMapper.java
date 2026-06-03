package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.dto.StudentCountDTO;
import com.xiaotiyun.school.manager.model.entity.VolunteerEntity;
import com.xiaotiyun.school.manager.model.req.VolunteerPageReqModel;
import com.xiaotiyun.school.manager.model.res.VolunteerResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 义工服务数据访问层
 */
@Mapper
public interface VolunteerMapper extends BaseMapper<VolunteerEntity> {

    List<VolunteerResModel> page(@Param("reqModel") VolunteerPageReqModel reqModel);


    List<StudentCountDTO> getVolunteerCount(@Param("classId")Long classId, @Param("startTime") LocalDateTime startTime, @Param("endTime")LocalDateTime endTime);

    List<VolunteerResModel> sumByStudent(Long schoolId, String schoolYear, Long studentId);
}