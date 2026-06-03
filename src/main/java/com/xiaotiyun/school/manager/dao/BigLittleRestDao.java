package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.BigLittleRestEntity;
import com.xiaotiyun.school.manager.model.req.BigLittleRestQueryReqModel;
import com.xiaotiyun.school.manager.model.res.BigLittleRestResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BigLittleRestDao extends BaseMapper<BigLittleRestEntity> {

    List<BigLittleRestResModel> page(@Param("reqModel") BigLittleRestQueryReqModel reqModel);
}