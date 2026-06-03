package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.StudentUsuallyTaskEntity;
import com.xiaotiyun.school.manager.model.req.StudentUsuallyPageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentUsuallyTaskCheckReqModel;
import com.xiaotiyun.school.manager.model.res.StudentUsuallyTaskPageResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentUsuallyTaskMapper extends BaseMapper<StudentUsuallyTaskEntity> {

    List<StudentUsuallyTaskPageResModel> page(@Param("taskIds") List<Long> taskIds, @Param("reqModel") StudentUsuallyPageReqModel reqModel);

    String check(@Param("schoolId") Long schoolId, @Param("reqModel") StudentUsuallyTaskCheckReqModel reqModel);
}