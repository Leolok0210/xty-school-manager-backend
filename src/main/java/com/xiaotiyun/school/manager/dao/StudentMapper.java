package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.excel.StudentExportDTO;
import com.xiaotiyun.school.manager.model.entity.StudentEntity;
import com.xiaotiyun.school.manager.model.req.StudentPageReqModel;
import com.xiaotiyun.school.manager.model.res.StudentListResModel;
import com.xiaotiyun.school.manager.model.res.StudentPageResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentMapper extends BaseMapper<StudentEntity> {

    List<StudentPageResModel> page(@Param("reqModel") StudentPageReqModel reqModel);

    List<StudentListResModel> listByClassId(Long classId);
}