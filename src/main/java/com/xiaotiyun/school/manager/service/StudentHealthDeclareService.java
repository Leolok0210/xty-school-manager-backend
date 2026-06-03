package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.entity.StudentHealthDeclareEntity;
import com.xiaotiyun.school.manager.model.req.StudentHealthDeclareAddReqModel;
import com.xiaotiyun.school.manager.model.req.StudentHealthDeclarePageReqModel;
import com.xiaotiyun.school.manager.model.res.StudentHealthDeclarePageResModel;

/**
 * 学生健康申报表服务接口
 * @author generated
 * @since 2025-8-26
 */
public interface StudentHealthDeclareService extends IService<StudentHealthDeclareEntity> {

    PageInfo<StudentHealthDeclarePageResModel> page(StudentHealthDeclarePageReqModel pageReqModel);

    Result addRecord(StudentHealthDeclareAddReqModel reqModel);
}
