package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.SchoolWeixinRelevanceEntity;

/**
 * 学校绑定企微表服务接口
 */
public interface SchoolWeixinRelevanceService extends IService<SchoolWeixinRelevanceEntity> {


    boolean exist(Long schoolId);
}