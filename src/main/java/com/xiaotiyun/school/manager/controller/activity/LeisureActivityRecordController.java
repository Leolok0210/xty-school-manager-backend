package com.xiaotiyun.school.manager.controller.activity;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.model.entity.LeisureActivityCoursesRecordEntity;
import com.xiaotiyun.school.manager.model.entity.LeisureActivityRecordEntity;
import com.xiaotiyun.school.manager.model.entity.SemesterEntity;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.LeisureActivityRecordResModel;
import com.xiaotiyun.school.manager.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Slf4j
@Api(tags = "余暇活动管理")
@RestController
@RequestMapping("/api/leisure/activity")
@RequiredArgsConstructor
public class LeisureActivityRecordController extends BasicController {
    private final SemesterService semesterService;
    private final LeisureActivityRecordService service;
    private final LeisureActivityCoursesRecordService coursesRecordService;
    private final NoticeService noticeService;
    private final ActivityStudentReportService activityStudentReportService;

    private final LanguageUtil languageUtil;

    /**
     * 新增余暇活动记录
     *
     * @param record 余暇活动记录实体
     * @return 操作结果
     */
    @ApiOperation("新增余暇活动")
    @PostMapping("/add")
    @SaCheckPermission("leisureActivity:add")
    public Result<Long> save(@RequestBody @Validated LeisureActivityRecordAddReqModel record) {
        record.setSchoolId(getSchoolId());
        LeisureActivityRecordEntity entity = new LeisureActivityRecordEntity();
        BeanUtils.copyProperties(record, entity);
        entity.setStatus(0);
        entity.setPublishStatus(0);
        // 检查活动名称是否重复
        long count = service.count(Wrappers.<LeisureActivityRecordEntity>lambdaQuery()
                .eq(LeisureActivityRecordEntity::getSchoolId, getSchoolId())
                .eq(LeisureActivityRecordEntity::getDepartment, record.getDepartment())
                .eq(LeisureActivityRecordEntity::getSchoolYear, record.getSchoolYear())
                .eq(LeisureActivityRecordEntity::getName, record.getName()));
        if (count > 0) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.ACTIVITY_NAME_DUPLICATED));
        }
        service.save(entity);
        return Result.success(entity.getId());
    }

    /**
     * 更新余暇活动记录
     *
     * @param record 余暇活动记录实体
     * @return 操作结果
     */
    @ApiOperation("更新余暇活动")
    @PutMapping("/update")
    @SaCheckPermission("leisureActivity:update")
    public Result<Boolean> update(@RequestBody @Validated LeisureActivityRecordAddReqModel record) {
        if (record.getId() == null) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
        }
        // 检查活动名称是否重复
        long count = service.count(Wrappers.<LeisureActivityRecordEntity>lambdaQuery()
                .ne(LeisureActivityRecordEntity::getId, record.getId())
                .eq(LeisureActivityRecordEntity::getSchoolId, getSchoolId())
                .eq(LeisureActivityRecordEntity::getDepartment, record.getDepartment())
                .eq(LeisureActivityRecordEntity::getSchoolYear, record.getSchoolYear())
                .eq(LeisureActivityRecordEntity::getName, record.getName()));
        if (count > 0) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.ACTIVITY_NAME_DUPLICATED));
        }
        record.setSchoolId(getSchoolId());
        LeisureActivityRecordEntity entity = new LeisureActivityRecordEntity();
        BeanUtils.copyProperties(record, entity);
        return Result.success(service.updateById(entity));
    }

    @ApiOperation("提前结束余暇活动")
    @PostMapping("/early/end")
    @SaCheckPermission("leisureActivity:earlyEnd")
    public Result<Boolean> earlyEnd(@Validated @RequestBody IdReqModel reqModel) {
        // 检查活动是否存在
        if(reqModel.getId() == null || reqModel.getId() <= 0){
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
        }
        Long id = reqModel.getId();
        LeisureActivityRecordEntity activity = service.getById(id);
        if (activity == null) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
        }
        // 获取当前时间
        if (activity.getDrawStatus() == 1 && activity.getSecondEndTime() == null) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
        }
        // 活动状态检查
        if (activity.getStatus() == 0) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
        }
        // 抽签学生
        try {
            activityStudentReportService.processActivity(activity,true);
        } catch (Exception e) {
            log.error("活动立即结束 {} 处理失败", activity.getId(), e);
        }

        return Result.success();
    }

    /**
     * 根据 ID 获取余暇活动记录
     *
     * @param id 记录 ID
     * @return 操作结果
     */
    @ApiOperation("获取余暇活动")
    @GetMapping("/get/{id}")
    @SaCheckPermission("leisureActivity:get")
    public Result<LeisureActivityRecordResModel> getDetail(@PathVariable Long id) {
        // 检查活动是否存在
        LeisureActivityRecordEntity byId = service.getById(id);
        if (byId == null) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
        }
        // 获取课程数量
        long courseNum = coursesRecordService.count(Wrappers.<LeisureActivityCoursesRecordEntity>lambdaQuery()
                .eq(LeisureActivityCoursesRecordEntity::getSchoolId, getSchoolId())
                .eq(LeisureActivityCoursesRecordEntity::getActivityId, id));
        // 拼接返回值
        LeisureActivityRecordResModel model = new LeisureActivityRecordResModel();
        BeanUtils.copyProperties(byId, model);
        model.setCourseNum(courseNum);
        SemesterEntity semester = semesterService.getById(byId.getSemesterId());
        if (semester != null && semester.getName() != null) {
            model.setSemesterName(semester.getName());
        }
        // 活动结束时间是否已过
        if (model.getSecondEndTime() != null) {
            if (model.getSecondEndTime().before(new Date())) {
                model.setStatus(3);
            }
        } else {
            if (model.getEndTime().before(new Date())) {
                model.setStatus(2);
            }
        }
        return Result.success(model);
    }

    /**
     * 根据 ID 获取余暇活动记录
     *
     * @param id 记录 ID
     * @return 操作结果
     */
    @ApiOperation("获取余暇活动-学生端(非鉴权)")
    @GetMapping("/student/get/{id}")
    public Result<LeisureActivityRecordResModel> getDetailByStudent(@PathVariable Long id) {
        LeisureActivityRecordEntity byId = service.getById(id);
        if (byId == null) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
        }
        LeisureActivityRecordResModel model = new LeisureActivityRecordResModel();
        BeanUtils.copyProperties(byId, model);
        SemesterEntity semester = semesterService.getById(byId.getSemesterId());
        if (semester != null && semester.getName() != null) {
            model.setSemesterName(semester.getName());
        }
        return Result.success(model);
    }

    /**
     * 根据 ID 删除余暇活动记录
     *
     * @param id 记录 ID
     * @return 操作结果
     */
    @ApiOperation("删除余暇活动")
    @DeleteMapping("/delete/{id}")
    @SaCheckPermission("leisureActivity:delete")
    public Result<Boolean> delete(@PathVariable Long id) {
        LeisureActivityRecordEntity activityRecord = service.getById(id);
        boolean result = service.removeById(id);
        if (result && activityRecord.getStatus() == 1 && activityRecord.getOpenWechatNotice() == 1) {
            noticeService.deleteLeisureNotice(activityRecord);
        }
        return Result.success();
    }

    /**
     * 复制余暇活动记录
     *
     * @param record 余暇活动记录实体
     * @return 操作结果
     */
    @ApiOperation("复制余暇活动")
    @PostMapping("/copy")
    @SaCheckPermission("leisureActivity:copy")
    public Result<Boolean> copy(@RequestBody @Validated LeisureActivityRecordAddReqModel record) {
        record.setSchoolId(getSchoolId());
        return service.copy(record);
    }

    /**
     * 余暇活动发布/召回
     *
     * @param id 记录 ID
     * @return 操作结果
     */
    @ApiOperation("余暇活动发布/召回")
    @GetMapping("/change/{id}/{status}")
    @SaCheckPermission("leisureActivity:change")
    public Result<Boolean> changeStatus(@ApiParam("活动id") @PathVariable Long id,
                                        @ApiParam("活动状态 (0-未发布, 1-已发布)") @PathVariable Integer status) {
        return service.changeStatus(id, status);
    }

    /**
     * 分页查询余暇活动记录
     *
     * @return 分页后的余暇活动记录
     */
    @ApiOperation("查询余暇活动记录分页")
    @PostMapping("/page")
    @SaCheckPermission("leisureActivity:page")
    public Result<PageInfo<LeisureActivityRecordResModel>> page(@RequestBody @Validated LeisureActivityRecordPageReqModel pageReqModel) {
        pageReqModel.setSchoolId(getSchoolId());
        return Result.success(service.selectPage(pageReqModel));
    }

    /**
     * 查询余暇全部活动记录
     *
     * @return 查询余暇全部活动记录
     */
    @ApiOperation("查询余暇全部活动记录")
    @PostMapping("/list")
    @SaCheckPermission("leisureActivity:list")
    public Result<List<LeisureActivityRecordResModel>> list(@RequestBody @Validated LeisureActivityRecordPageReqModel pageReqModel) {
        pageReqModel.setSchoolId(getSchoolId());
        return Result.success(service.listByReq(pageReqModel));
    }

    /**
     * 首页查询余暇活动记录-学生端(非鉴权)
     *
     * @return 查询结果
     */
    @ApiOperation("首页查询余暇活动记录-学生端(非鉴权)")
    @PostMapping("/student/list")
    public Result<List<LeisureActivityRecordResModel>> pageByStudent(@RequestBody @Validated LeisureActivityRecordIndexReqModel reqModel) {
        return Result.success(service.listByStudent(reqModel));
    }

    @ApiOperation("企微通知开关")
    @PostMapping("/wechat/notice")
    @SaCheckPermission("leisureActivity:wechat:notice")
    public Result<Boolean> wechatNotice(@RequestBody @Validated LeisureActivityWechatNoticeReqModel reqModel) {
        boolean result = false;
        LeisureActivityRecordEntity entity = service.getById(reqModel.getId());
        if (entity != null) {
            entity.setOpenWechatNotice(reqModel.getOpenWechatNotice());
            result = service.updateById(entity);
            if (result) {
                if (reqModel.getOpenWechatNotice() == 1) {
                    // 开启企微通知，创建剩余需要发送通知任务
                    noticeService.createLeisureNotice(entity);
                } else {
                    // 关闭企微通知，删除剩余未执行的发送通知任务
                    noticeService.deleteLeisureNotice(entity);
                }
            }
        }
        return Result.success(result);
    }
}
