package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.LeisureActivityRecordEntity;
import com.xiaotiyun.school.manager.model.req.LeisureActivityRecordPageReqModel;
import com.xiaotiyun.school.manager.model.res.LeisureActivityRecordResModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 余暇活动记录表 Mapper 接口
 */
@Mapper
public interface LeisureActivityRecordDao extends BaseMapper<LeisureActivityRecordEntity> {

    List<LeisureActivityRecordResModel> page(LeisureActivityRecordPageReqModel reqModel);
}
