package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.ActivityStudentReportEntity;
import com.xiaotiyun.school.manager.model.entity.LeisureActivityRecordEntity;
import com.xiaotiyun.school.manager.model.req.ActivityStudentReportQueryReqModel;
import com.xiaotiyun.school.manager.model.req.ActivityStudentReportListReqModel;
import com.xiaotiyun.school.manager.model.req.ActivityStudentReportRemoveReqModel;
import com.xiaotiyun.school.manager.model.req.ActivityStudentReportTransferReqModel;
import com.xiaotiyun.school.manager.model.req.ImportActivityStudentReportReqModel;
import com.xiaotiyun.school.manager.model.req.ActivityStudentReportExportReqModel;
import com.xiaotiyun.school.manager.model.res.ActivityStudentReportQueryResModel;
import com.xiaotiyun.school.manager.model.res.ActivityStudentReportListResModel;
import com.xiaotiyun.school.manager.model.res.ImportActivityStudentReportResModel;
import com.xiaotiyun.school.manager.model.res.ActivityStudentReportExportResModel;

import java.util.List;

/**
 * 活动已匹配表服务接口
 */
public interface ActivityStudentReportService extends IService<ActivityStudentReportEntity> {
    
    /**
     * 导入活动匹配
     *
     * @param reqModel 导入请求参数
     * @return 导入结果
     */
    ImportActivityStudentReportResModel importActivityStudentReport(ImportActivityStudentReportReqModel reqModel);

    /**
     * 查询学生无选课情况列表
     *
     * @param reqModel 查询请求参数
     * @return 学生选课情况列表
     */
    PageInfo<ActivityStudentReportQueryResModel> getStudentCourseList(ActivityStudentReportQueryReqModel reqModel);

    /**
     * 查询无课程总人数
     *
     * @param activityId 活动ID
     * @return 无课程总人数
     */
    Integer getNoCourseStudentCount(Long activityId);

    /**
     * 查询活动匹配列表
     *
     * @param reqModel 查询请求参数
     * @return 活动匹配列表
     */
    PageInfo<ActivityStudentReportListResModel> getActivityStudentReportList(ActivityStudentReportListReqModel reqModel);

    /**
     * 批量移除活动匹配
     *
     * @param reqModel 移除请求参数
     * @return 移除结果
     */
    Boolean batchRemoveActivityStudentReport(ActivityStudentReportRemoveReqModel reqModel);

    /**
     * 转课程
     *
     * @param reqModelList 转课程请求参数列表
     * @return 转课程结果
     */
    Boolean transferCourse(List<ActivityStudentReportTransferReqModel> reqModelList);

    /**
     * 导出活动匹配数据
     *
     * @param reqModel 导出请求参数
     * @return 导出结果
     */
    ActivityStudentReportExportResModel exportActivityStudentReport(ActivityStudentReportExportReqModel reqModel);

    /**
     * 定时任务处理报名逻辑
     * @param activity
     */
    void processActivity(LeisureActivityRecordEntity activity, boolean endNow);
} 