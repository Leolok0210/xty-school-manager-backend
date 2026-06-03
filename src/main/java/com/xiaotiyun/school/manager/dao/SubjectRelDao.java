package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.SubjectRelEntity;
import com.xiaotiyun.school.manager.model.req.SubjectRelGroupQueryReqModel;
import com.xiaotiyun.school.manager.model.res.SubjectRelResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SubjectRelDao extends BaseMapper<SubjectRelEntity> {
    List<SubjectRelResModel> selectSubjectAndRelByGroup(@Param("req") SubjectRelGroupQueryReqModel req);

    List<SubjectRelResModel> selectSubjectAndRelByIds(List<Long> ids);
}