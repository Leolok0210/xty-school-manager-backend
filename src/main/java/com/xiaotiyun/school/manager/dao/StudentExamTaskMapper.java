package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.StudentExamTaskEntity;
import com.xiaotiyun.school.manager.model.req.StudentExamPageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentExamTaskCheckReqModel;
import com.xiaotiyun.school.manager.model.res.StudentExamTaskPageResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentExamTaskMapper extends BaseMapper<StudentExamTaskEntity> {

    List<StudentExamTaskPageResModel> page(@Param("taskIds") List<Long> taskIds, @Param("reqModel") StudentExamPageReqModel reqModel);

    String check(@Param("schoolId") Long schoolId, @Param("reqModel") StudentExamTaskCheckReqModel reqModel);
} 