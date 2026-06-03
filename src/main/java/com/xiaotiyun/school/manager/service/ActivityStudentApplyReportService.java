package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.dto.ActivityStudentApplyReportQueryDTO;
import com.xiaotiyun.school.manager.model.entity.ActivityStudentApplyReportEntity;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;

import java.util.List;

/**
 * 活动报名表服务接口
 */
public interface ActivityStudentApplyReportService extends IService<ActivityStudentApplyReportEntity> {

    /**
     * 一键公布报名结果
     *
     * @param reqModel 公布请求参数
     * @return 公布结果
     */
    Boolean publishActivityResult(ActivityStudentApplyReportPublishReqModel reqModel);

    /**
     * 发起二次报名
     *
     * @param reqModel 二次报名请求参数
     * @return 发起结果
     */
    Boolean startSecondApply(ActivityStudentApplyReportSecondApplyReqModel reqModel);

    /**
     * 查询活动学生管理列表
     *
     * @param reqModel 查询请求参数
     * @return 活动学生管理列表
     */
    PageInfo<ActivityStudentApplyReportListResModel> getActivityStudentList(Long schoolId, Long userId, ActivityStudentApplyReportListReqModel reqModel);

    /**
     * 查询二次报名和没有二次报名但未匹配课程的学生
     *
     * @param schoolId   学校ID
     * @param department 学部
     * @param schoolYear 学年
     * @param activityId 活动ID
     * @return 分页学生列表
     */
    PageInfo<ActivityStudentApplyReportQueryDTO> querySecondApplyAndUnmatchedStudents(
            Long schoolId, Integer department, String schoolYear, Long activityId, ActivityStudentApplyReportSecondListReqModel reqModel);

    /**
     * 查询活动二次报名总人数
     *
     * @param reqModel 查询请求参数
     * @return 二次报名总人数
     */
    Integer getSecondApplyCount(ActivityStudentApplyReportSecondCountReqModel reqModel);

    /**
     * 学生报名
     *
     * @param reqModel 报名请求参数
     * @return 报名结果
     */
    Boolean studentApply(ActivityStudentApplyReportApplyReqModel reqModel);

    /**
     * 我的课程列表-学生端
     *
     * @param reqModel 查询请求参数
     * @return 我的课程列表
     */
    List<ActivityStudentApplyReportCurrentActivityResModel> getMyCourseList(ActivityStudentApplyReportMyCourseReqModel reqModel);

    /**
     * 已录取名单
     *
     * @param schoolId
     * @param userId
     * @param reqModel
     * @return
     */
    PageInfo<ActivityStudentApplyAdmittedListResModel> admittedList(Long schoolId, Long userId, ActivityStudentApplyAdmittedListReqModel reqModel);

    /**
     * 已录取名单导出
     *
     * @param schoolId
     * @param userId
     * @param reqModel
     * @return
     */
    String admittedExport(Long schoolId, Long userId, ActivityStudentApplyAdmittedListReqModel reqModel);

    /**
     * 已报名名单
     *
     * @param schoolId
     * @param userId
     * @param reqModel
     * @return
     */
    PageInfo<ActivityStudentApplyRegisteredListResModel> registeredList(Long schoolId, Long userId, ActivityStudentApplyRegisteredListReqModel reqModel);

    /**
     * 已报名名单导出
     *
     * @param schoolId
     * @param userId
     * @param reqModel
     * @return
     */
    String registeredExport(Long schoolId, Long userId, ActivityStudentApplyRegisteredListReqModel reqModel);

    /**
     * 已报名名单
     *
     * @param schoolId
     * @param userId
     * @param reqModel
     * @return
     */
    PageInfo<ActivityStudentApplyNotRegisteredListResModel> notRegisteredList(Long schoolId, Long userId, ActivityStudentApplyNotRegisteredListReqModel reqModel);

    /**
     * 已报名名单导出
     *
     * @param schoolId
     * @param userId
     * @param reqModel
     * @return
     */
    String notRegisteredExport(Long schoolId, Long userId, ActivityStudentApplyNotRegisteredListReqModel reqModel);
}