package com.xiaotiyun.school.manager.service;

import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.Subject;
import com.xiaotiyun.school.manager.model.req.SubjectQueryReqModel;
import com.xiaotiyun.school.manager.model.res.SubjectDetailResModel;
import com.xiaotiyun.school.manager.model.res.SubjectSimpleResModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SubjectService {
    void createSubjects(List<Subject> subjects);
    void updateSubject(Subject subject);
    void deleteSubject(Long id, Long aLong);
    Subject getSubjectById(Long id);
    PageInfo<SubjectDetailResModel> getSubjectList(SubjectQueryReqModel reqModel);

    List<SubjectSimpleResModel> getSubjectsBySchoolAndDepartment(Long schoolId, Integer departmentId);

    Long importSubject(MultipartFile file, Long schoolId);

    List<SubjectDetailResModel> getSubjects(List<Long> ids);


    List<SubjectDetailResModel> getSubjects(Long schoolId,String name);

}