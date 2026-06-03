package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.UserEntity;
import com.xiaotiyun.school.manager.model.req.UserQueryReqModel;
import com.xiaotiyun.school.manager.model.res.TeacherListResModel;
import com.xiaotiyun.school.manager.model.res.UserDetailResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserDao extends BaseMapper<UserEntity> {


    List<TeacherListResModel> selectTeachersBySchool(Long schoolId);

    List<UserDetailResModel> queryTeacherList(@Param("reqModel") UserQueryReqModel reqModel,@Param("schoolId") Long schoolId);
}