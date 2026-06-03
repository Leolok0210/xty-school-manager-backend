package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.CrossBorderStudentEntity;
import com.xiaotiyun.school.manager.model.req.CrossBorderStudentReqModel;
import com.xiaotiyun.school.manager.model.res.CrossBorderStudentResModel;

/**
 * 跨境学生登记服务接口
 */
public interface CrossBorderStudentService extends IService<CrossBorderStudentEntity> {

    /**
     * 保存跨境学生信息
     *
     * @param reqModel 请求参数
     * @return 保存结果
     */
    Long save(CrossBorderStudentReqModel reqModel);

    /**
     * 保存跨境学生信息
     *
     * @param reqModel 请求参数
     * @return 保存结果
     */
    void update(Long studentId, CrossBorderStudentReqModel reqModel);

    /**
     * 获取跨境学生信息
     *
     * @param studentId 学生ID
     * @return 跨境学生信息
     */
    CrossBorderStudentResModel info(Long studentId);
}