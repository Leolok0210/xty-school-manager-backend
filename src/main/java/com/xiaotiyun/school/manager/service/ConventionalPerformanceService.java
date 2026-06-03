package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.ConventionalPerformanceEntity;
import com.xiaotiyun.school.manager.model.req.ConventionalPerformancePageReqModel;
import com.xiaotiyun.school.manager.model.req.ConventionalPerformanceSaveReqModel;
import com.xiaotiyun.school.manager.model.req.ConventionalPerformanceUpdateReqModel;
import com.xiaotiyun.school.manager.model.req.StudentPerformanceTotalReqModel;
import com.xiaotiyun.school.manager.model.res.ConventionalPerformancePageResModel;
import com.xiaotiyun.school.manager.model.res.StudentPerformanceTotalResModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ConventionalPerformanceService extends IService<ConventionalPerformanceEntity> {

    PageInfo<ConventionalPerformancePageResModel> page(Long schoolId, ConventionalPerformancePageReqModel reqModel);

    void save(Long schoolId, Long userId, ConventionalPerformanceSaveReqModel reqModel);

    void update(Long id, ConventionalPerformanceUpdateReqModel reqModel);

    void delete(Long id);

    /**
     * 导入记录
     */
    Long importRecord(Long schoolId, Long userId, String sid, Long term, Long classId, MultipartFile file);


    /**
     * 导出
     */
    String export(Long schoolId, ConventionalPerformancePageReqModel reqModel);

    List<StudentPerformanceTotalResModel> getTotal(StudentPerformanceTotalReqModel reqModel);
}