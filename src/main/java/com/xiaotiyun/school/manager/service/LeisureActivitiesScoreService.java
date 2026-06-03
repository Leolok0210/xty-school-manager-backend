package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.LeisureActivitiesScoreEntity;
import com.xiaotiyun.school.manager.model.req.LeisureActivitiesScorePageReqModel;
import com.xiaotiyun.school.manager.model.req.LeisureActivitiesScoreSaveReqModel;
import com.xiaotiyun.school.manager.model.res.LeisureActivitiesScorePageResModel;
import org.springframework.web.multipart.MultipartFile;

/**
 * 余暇活动成绩信息Service层接口
 */
public interface LeisureActivitiesScoreService extends IService<LeisureActivitiesScoreEntity> {

    /**
     * 分页查询余暇活动成绩信息
     *
     * @param reqModel 查询参数
     * @return 分页结果
     */
    PageInfo<LeisureActivitiesScorePageResModel> page(Long schoolId, Long userId, LeisureActivitiesScorePageReqModel reqModel);

    /**
     * 新增余暇活动成绩信息
     *
     * @param reqModel
     * @return
     */
    LeisureActivitiesScoreEntity save(Long schoolId, LeisureActivitiesScoreSaveReqModel reqModel);

    /**
     * 修改余暇活动成绩信息
     *
     * @param reqModel
     * @return
     */
    LeisureActivitiesScoreEntity update(Long id, LeisureActivitiesScoreSaveReqModel reqModel);

    /**
     * 导入
     */
    Long importScore(Long schoolId, Long activityId, MultipartFile file);

    /**
     * 导出
     * @param reqModel
     * @return
     */
    String export(Long schoolId, Long userId, LeisureActivitiesScorePageReqModel reqModel);
}