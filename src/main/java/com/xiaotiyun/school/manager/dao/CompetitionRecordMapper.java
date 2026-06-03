package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.dto.CompetitionStudentCountDTO;
import com.xiaotiyun.school.manager.model.entity.CompetitionRecordEntity;
import com.xiaotiyun.school.manager.model.req.CompetitionPageReqModel;
import com.xiaotiyun.school.manager.model.req.CompetitionRecordPageReqModel;
import com.xiaotiyun.school.manager.model.req.CompetitionRecordStudentPageReqModel;
import com.xiaotiyun.school.manager.model.res.CompetitionRecordResModel;
import com.xiaotiyun.school.manager.model.res.CompetitionStudentPageResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CompetitionRecordMapper extends BaseMapper<CompetitionRecordEntity> {

    /**
     * 获取参与比赛列表
     */
    List<Long> partakeCompetitionList(@Param("reqModel") CompetitionPageReqModel reqModel);

    List<CompetitionRecordResModel> selectRecordPage(@Param("reqModel") CompetitionRecordPageReqModel reqModel);

    List<CompetitionStudentPageResModel> selectStudentRecordPage(@Param("reqModel") CompetitionRecordStudentPageReqModel reqModel);

    List<CompetitionStudentCountDTO> getCountStudent(@Param("semesterStart") LocalDateTime semesterStart,
                                                     @Param("semesterEnd") LocalDateTime semesterEnd,
                                                     @Param("classId") Long classId);
}