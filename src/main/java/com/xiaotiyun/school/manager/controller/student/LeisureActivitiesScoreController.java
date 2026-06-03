package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.model.entity.LeisureActivitiesScoreEntity;
import com.xiaotiyun.school.manager.model.req.LeisureActivitiesScorePageReqModel;
import com.xiaotiyun.school.manager.model.req.LeisureActivitiesScoreSaveReqModel;
import com.xiaotiyun.school.manager.model.res.LeisureActivitiesScorePageResModel;
import com.xiaotiyun.school.manager.service.LeisureActivitiesScoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "余暇活动成绩管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/leisure/activities/score")
public class LeisureActivitiesScoreController extends BasicController {
    private final LanguageUtil languageUtil;
    private final LeisureActivitiesScoreService leisureActivitiesScoreService;

    @ApiOperation("分页查询列表")
    @GetMapping("/page")
    @SaCheckPermission("leisure:activities:score:page")
    public Result<PageInfo<LeisureActivitiesScorePageResModel>> page(@ApiParam("查询参数") @Validated LeisureActivitiesScorePageReqModel reqModel) {
        return Result.success(leisureActivitiesScoreService.page(getSchoolId(), getUserId(), reqModel));
    }

    @ApiOperation("新增成绩")
    @PostMapping("/add")
    @SaCheckPermission("leisure:activities:score:add")
    public Result<Long> save(@Validated @RequestBody LeisureActivitiesScoreSaveReqModel reqModel) {
        LeisureActivitiesScoreEntity entity = leisureActivitiesScoreService.save(getSchoolId(), reqModel);
        return Result.success(entity.getId());
    }

    @ApiOperation("修改成绩")
    @PutMapping("/update/{id}")
    @SaCheckPermission("leisure:activities:score:update")
    public Result<Void> update(
            @ApiParam("id") @PathVariable Long id,
            @Validated @RequestBody LeisureActivitiesScoreSaveReqModel reqModel) {
        leisureActivitiesScoreService.update(id, reqModel);
        return Result.success();
    }

    @ApiOperation("导入成绩")
    @PostMapping("/import")
    @SaCheckPermission("leisure:activities:score:import")
    public Result<Long> importScore(
            @ApiParam("Excel文件") @RequestPart("uploadFile") MultipartFile file,
            @ApiParam("活动id") @RequestParam Long activityId) {
        try {
            Long importId = leisureActivitiesScoreService.importScore(getSchoolId(), activityId, file);
            return Result.success(importId);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(ResultCode.FILE_UPLOAD_FAILED.getMessageCode()) + ":" + e.getMessage());
        }
    }

    @ApiOperation("导出成绩")
    @GetMapping("/export")
    @SaCheckPermission("leisure:activities:score:export")
    public Result<String> exportScore(@Validated LeisureActivitiesScorePageReqModel reqModel) {
        return Result.success(leisureActivitiesScoreService.export(getSchoolId(), getUserId(), reqModel));
    }
}