package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.StudentMedicalRecordEntity;
import com.xiaotiyun.school.manager.model.req.MedicalRecordReqModel;
import com.xiaotiyun.school.manager.model.res.MedicalRecordResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentMedicalRecordDao extends BaseMapper<StudentMedicalRecordEntity> {

    public List<MedicalRecordResModel> page(@Param("reqModel") MedicalRecordReqModel reqModel);
}
