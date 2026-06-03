package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.LeisureActivityCoursesRecordEntity;
import com.xiaotiyun.school.manager.model.req.LeisureActivityCoursesQueryRecordReq;
import com.xiaotiyun.school.manager.model.res.LeisureActivityCoursesRecordRes;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LeisureActivityCoursesRecordDao extends BaseMapper<LeisureActivityCoursesRecordEntity> {

    List<LeisureActivityCoursesRecordRes> pageAndPre(LeisureActivityCoursesQueryRecordReq req);

    List<LeisureActivityCoursesRecordRes> pageAndApply(LeisureActivityCoursesQueryRecordReq req);

    List<LeisureActivityCoursesRecordRes> pageAndApplyByStudent(LeisureActivityCoursesQueryRecordReq req);
}
