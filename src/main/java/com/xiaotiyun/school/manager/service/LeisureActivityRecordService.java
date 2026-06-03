package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.entity.LeisureActivityRecordEntity;
import com.xiaotiyun.school.manager.model.req.LeisureActivityRecordAddReqModel;
import com.xiaotiyun.school.manager.model.req.LeisureActivityRecordIndexReqModel;
import com.xiaotiyun.school.manager.model.req.LeisureActivityRecordPageReqModel;
import com.xiaotiyun.school.manager.model.res.LeisureActivityRecordResModel;

import java.util.List;

/**
 * 余暇活动记录表 Service 接口
 */
public interface LeisureActivityRecordService extends IService<LeisureActivityRecordEntity> {
    PageInfo<LeisureActivityRecordResModel> selectPage(LeisureActivityRecordPageReqModel reqModel);

//    List<LeisureActivityRecordResModel> listByReq(LeisureActivityRecordPageReqModel reqModel);

    List<LeisureActivityRecordResModel> listByReq(LeisureActivityRecordPageReqModel reqModel);

    List<LeisureActivityRecordResModel> listByStudent(LeisureActivityRecordIndexReqModel reqModel);

    Result<Boolean> changeStatus(Long id, Integer status);

    Result<Boolean> copy(LeisureActivityRecordAddReqModel record);
}
