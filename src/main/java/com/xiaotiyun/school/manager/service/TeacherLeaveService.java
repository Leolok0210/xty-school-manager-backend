package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.TeacherLeaveEntity;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.TeacherLeavePageResModel;
import com.xiaotiyun.school.manager.model.res.TeacherLeaveReportResModel;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface TeacherLeaveService extends IService<TeacherLeaveEntity> {

    PageInfo<TeacherLeavePageResModel> page(TeacherLeavePageReqModel reqModel);

    Long getPendingApproval(Long schoolId);

    void save(TeacherLeaveSaveReqModel reqModel);

    void update(Long id, TeacherLeaveSaveReqModel reqModel);

    void delete(Long id);

    String export(TeacherLeavePageReqModel reqModel);

    void handle(TeacherLeaveHandleReqModel reqModel);

    List<TeacherLeaveReportResModel> report(TeacherLeaveReportReqModel reqModel);

    ResponseEntity<byte[]> reportExport(TeacherLeaveReportReqModel reqModel) throws UnsupportedEncodingException;

    void start(Long schoolId, Long userId, TeacherLeaveStartReqModel reqModel);
}