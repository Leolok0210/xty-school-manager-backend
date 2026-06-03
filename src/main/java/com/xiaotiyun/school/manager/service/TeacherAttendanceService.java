package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.TeacherAttendanceEntity;
import com.xiaotiyun.school.manager.model.req.TeacherAttendancePageReqModel;
import com.xiaotiyun.school.manager.model.req.TeacherAttendanceStatisticsReqModel;
import com.xiaotiyun.school.manager.model.req.TeacherAttendanceUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.TeacherAttendancePageResModel;
import com.xiaotiyun.school.manager.model.res.TeacherAttendanceStatisticsResModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TeacherAttendanceService extends IService<TeacherAttendanceEntity> {
    PageInfo<TeacherAttendancePageResModel> page(TeacherAttendancePageReqModel reqModel);

    void update(Long id, TeacherAttendanceUpdateReqModel reqModel);

    void delete(Long id);

    /**
     * 导入记录
     */
    Long importRecord(Long schoolId, MultipartFile file);

    String export(TeacherAttendancePageReqModel reqModel);

    List<TeacherAttendanceStatisticsResModel> statistics(TeacherAttendanceStatisticsReqModel reqModel);
}