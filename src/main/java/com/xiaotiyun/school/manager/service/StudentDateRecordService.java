package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.entity.StudentDateRecordEntity;
import com.xiaotiyun.school.manager.model.req.StudentDateRecordUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.StudentDateRecordResModel;

public interface StudentDateRecordService extends IService<StudentDateRecordEntity> {

    /**
     * 根据ID获取学生日期记录
     * 该方法根据记录ID查询学生日期记录的详细信息。
     */
    StudentDateRecordResModel getStudentDateRecordById(Long id, Long schoolId);

    /**
     * 更新学生日期记录
     * 该方法用于更新已存在的学生日期记录，可以修改签到、签退时间等信息。
     */
    Result<String> updateStudentDateRecord(StudentDateRecordUpdateReqModel reqModel);
}