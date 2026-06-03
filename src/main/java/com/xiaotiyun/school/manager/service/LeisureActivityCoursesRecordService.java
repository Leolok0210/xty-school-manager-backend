package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.LeisureActivityCoursesRecordEntity;
import com.xiaotiyun.school.manager.model.req.LeisureActivityCoursesQueryRecordReq;
import com.xiaotiyun.school.manager.model.res.LeisureActivityCoursesRecordRes;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 余暇活动课程记录表 Service 接口
 */
public interface LeisureActivityCoursesRecordService extends IService<LeisureActivityCoursesRecordEntity> {

    PageInfo<LeisureActivityCoursesRecordRes> pageAndPre(LeisureActivityCoursesQueryRecordReq reqModel);

    PageInfo<LeisureActivityCoursesRecordRes> pageAndApply(LeisureActivityCoursesQueryRecordReq reqModel);

    List<LeisureActivityCoursesRecordRes> listAndApply(LeisureActivityCoursesQueryRecordReq reqModel);

    List<LeisureActivityCoursesRecordRes> pageAndApplyByStudent(LeisureActivityCoursesQueryRecordReq reqModel);

    Long importCourses(MultipartFile file, Long activityId, Long schoolId);
}
