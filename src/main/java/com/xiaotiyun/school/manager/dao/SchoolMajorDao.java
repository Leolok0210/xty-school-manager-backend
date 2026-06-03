package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.SchoolMajor;
import com.xiaotiyun.school.manager.model.req.SchoolMajorQueryReqModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SchoolMajorDao extends BaseMapper<SchoolMajor> {

    // 继承了BaseMapper，已经包含了基本的CRUD方法
    // 如果有特殊需求，可以在这里添加自定义方法



    List<SchoolMajor> getSchoolMajorList(@Param("reqModel") SchoolMajorQueryReqModel  reqModel,@Param("subjectIds") List<Long> subjectIds);
}