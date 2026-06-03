package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.TranscriptRecordEntity;
import com.xiaotiyun.school.manager.model.req.TranscriptRecordQueryReqModel;
import com.xiaotiyun.school.manager.model.res.TranscriptRecordResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TranscriptRecordDao extends BaseMapper<TranscriptRecordEntity> {

    List<TranscriptRecordResModel> page(@Param("reqModel") TranscriptRecordQueryReqModel reqModel);
}