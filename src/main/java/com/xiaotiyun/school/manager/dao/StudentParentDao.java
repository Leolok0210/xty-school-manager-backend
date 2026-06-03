package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.StudentParentEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生家长信息DAO接口
 */
@Mapper
public interface StudentParentDao extends BaseMapper<StudentParentEntity> {

}
