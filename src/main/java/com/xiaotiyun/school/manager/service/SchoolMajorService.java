package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.QualityEvaluationIndicatorEntity;
import com.xiaotiyun.school.manager.model.entity.SchoolMajor;
import com.xiaotiyun.school.manager.model.req.SchoolMajorQueryReqModel;
import com.xiaotiyun.school.manager.model.res.SchoolMajorDetailResModel;

import java.util.List;
import java.util.Map;

public interface SchoolMajorService extends IService<SchoolMajor> {
    void createSchoolMajors(List<SchoolMajor> schoolMajors);
    void updateSchoolMajor(SchoolMajor schoolMajor);
    void deleteSchoolMajor(Long id);
    SchoolMajor getSchoolMajorById(Long id);
    PageInfo<SchoolMajorDetailResModel> getSchoolMajorList(SchoolMajorQueryReqModel reqModel);

    //根据专业名称查询，学校id
    List<SchoolMajor> getSchoolMajorByName(String name, Long schoolId);

    //根据学部和学校id
    List<SchoolMajor> getSchoolMajorByDepartmentAndSchoolId(Integer departmentId, Long schoolId);

    Long getMajorIdByName(String professional, Long schoolId);

    Map<String, SchoolMajor> getSchoolMajorMapBySchoolId(Long schoolId);
}
