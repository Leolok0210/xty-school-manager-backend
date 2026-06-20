package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.CampusPhotoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CampusPhotoDao extends BaseMapper<CampusPhotoEntity> {

    List<CampusPhotoEntity> findBySchoolId(@Param("schoolId") Long schoolId);
}
