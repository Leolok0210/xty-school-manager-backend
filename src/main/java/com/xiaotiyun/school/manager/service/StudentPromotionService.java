package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.StudentPromotionEntity;
import com.xiaotiyun.school.manager.model.req.StudentPromotionPageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentPromotionSaveReqModel;
import com.xiaotiyun.school.manager.model.req.StudentPromotionUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.StudentPromotionResModel;

import java.util.List;

public interface StudentPromotionService extends IService<StudentPromotionEntity> {

    /**
     * 已登记学生列表
     */
    List<Long> studentList(Long schoolId, String schoolYear, Long classId);

    /**
     * 分页查询升留级记录
     */
    PageInfo<StudentPromotionResModel> page(Long schoolId, StudentPromotionPageReqModel reqModel);

    /**
     * 创建升留级记录
     */
    void save(Long schoolId, StudentPromotionSaveReqModel reqModel);

    /**
     * 更新升留级记录
     */
    void update(Long id, StudentPromotionUpdateReqModel reqModel);

    /**
     * 删除记录
     */
    void delete(Long id);

    String export(Long schoolId, StudentPromotionPageReqModel reqModel);
}