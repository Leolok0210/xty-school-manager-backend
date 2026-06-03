package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.Subject;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;
@Mapper
public interface SubjectDao extends BaseMapper<Subject> {

}
