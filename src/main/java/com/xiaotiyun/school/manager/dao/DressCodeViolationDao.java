package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.DressCodeViolationEntity;
import com.xiaotiyun.school.manager.model.req.DressCodeViolationQueryReqModel;
import com.xiaotiyun.school.manager.model.res.DressCodeViolationResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DressCodeViolationDao extends BaseMapper<DressCodeViolationEntity> {

    List<DressCodeViolationResModel> page(@Param("reqModel") DressCodeViolationQueryReqModel reqModel);
}