package com.xiaotiyun.school.manager.dao;

import com.xiaotiyun.school.manager.model.entity.GradeGroup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author Akame
* @description 针对表【grade_group( 级组表)】的数据库操作Mapper
* @createDate 2025-02-11 16:47:23
* @Entity com.xiaotiyun.school.manager.model.entity.GradeGroup
*/
@Mapper
public interface GradeGroupMapper extends BaseMapper<GradeGroup> {

    List<GradeGroup> getGradeGroupMapBySchoolId(Long schoolId);
}




