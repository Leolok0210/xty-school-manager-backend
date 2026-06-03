package com.xiaotiyun.school.manager.helper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaotiyun.school.manager.model.entity.CourseScheduleEntity;
import com.xiaotiyun.school.manager.model.entity.SchoolMajor;
import com.xiaotiyun.school.manager.model.entity.StudentUsuallyTaskEntity;
import com.xiaotiyun.school.manager.model.entity.TeachingSetting;
import com.xiaotiyun.school.manager.service.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SubjectCheckHelper {

    @Resource
    private TeachingSettingService teachingSettingService;

    @Resource
    private SchoolMajorService schoolMajorService;

    @Resource
    private CourseScheduleService courseScheduleService;

    @Resource
    private StudentUsuallyTaskService studentUsuallyTaskService;


    @Resource
    private StudentExamTaskService studentExamTaskService;

    /**
     * true校验通过
     * @param schoolId
     * @param subjectId
     * @return
     */
    //专业设定、任课老师设定、排课设定、平时成绩、考试成绩
    public boolean checkSubject(Long schoolId, Long subjectId) {

        //专业设定
        LambdaQueryWrapper<SchoolMajor> schoolMajorQueryWrapper = new LambdaQueryWrapper<>();
        schoolMajorQueryWrapper.eq(SchoolMajor::getSchoolId, schoolId);
        schoolMajorQueryWrapper.eq(SchoolMajor::getMajorSubjects, subjectId);
        if(schoolMajorService.count(schoolMajorQueryWrapper) > 0)
        {
            return false;
        }
        //任课老师设定
        LambdaQueryWrapper<TeachingSetting> teachingSettingQueryWrapper = new LambdaQueryWrapper<>();
        teachingSettingQueryWrapper.eq(TeachingSetting::getSchoolId, schoolId);
        teachingSettingQueryWrapper.eq(TeachingSetting::getSubjectId, subjectId);
        if(teachingSettingService.count(teachingSettingQueryWrapper) > 0)
        {
            return false;
        }
        //排课设定
        LambdaQueryWrapper<CourseScheduleEntity> courseScheduleQueryWrapper = new LambdaQueryWrapper<>();
        courseScheduleQueryWrapper.eq(CourseScheduleEntity::getSchoolId, schoolId);
        courseScheduleQueryWrapper.eq(CourseScheduleEntity::getSubjectId, subjectId);
        if(courseScheduleService.count(courseScheduleQueryWrapper) > 0)
        {
            return false;
        }
        //平时成绩
        LambdaQueryWrapper<StudentUsuallyTaskEntity> studentUsuallyTaskQueryWrapper = new LambdaQueryWrapper<>();
        studentUsuallyTaskQueryWrapper.eq(StudentUsuallyTaskEntity::getSchoolId, schoolId);
        studentUsuallyTaskQueryWrapper.eq(StudentUsuallyTaskEntity::getSubjectId, subjectId);
        if(studentUsuallyTaskService.count(studentUsuallyTaskQueryWrapper) > 0)
        {
            return false;
        }
        return true;
    }

}
