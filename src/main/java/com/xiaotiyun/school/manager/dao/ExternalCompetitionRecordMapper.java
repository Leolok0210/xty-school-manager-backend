package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.dto.ExternalCompetitionRecordDTO;
import com.xiaotiyun.school.manager.model.dto.StudentCountDTO;
import com.xiaotiyun.school.manager.model.entity.ExternalCompetitionRecordEntity;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionQueryReqModel;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface ExternalCompetitionRecordMapper extends BaseMapper<ExternalCompetitionRecordEntity> {
    /**
     * 获取参与比赛列表
     */
    List<Long> partakeCompetitionList(@Param("reqModel") ExternalCompetitionQueryReqModel reqModel);


    List<StudentCountDTO> countRecordsByDateRange(@Param("semesterStart") LocalDate semesterStart, @Param("semesterEnd") LocalDate semesterEnd, @Param("classId") Long classId);

    List<ExternalCompetitionRecordDTO> getCompetitionRecords(@Param("semesterStart") LocalDateTime semesterStart, @Param("semesterEnd") LocalDateTime semesterEnd,  @Param("classId") Long classId);

}