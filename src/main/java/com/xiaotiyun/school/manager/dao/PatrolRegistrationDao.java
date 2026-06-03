package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.PatrolRegistrationEntity;
import com.xiaotiyun.school.manager.model.req.PatrolRegistrationQueryReqModel;
import com.xiaotiyun.school.manager.model.res.PatrolRegistrationResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PatrolRegistrationDao extends BaseMapper<PatrolRegistrationEntity> {

    List<PatrolRegistrationResModel> page(@Param("reqModel") PatrolRegistrationQueryReqModel reqModel);
}