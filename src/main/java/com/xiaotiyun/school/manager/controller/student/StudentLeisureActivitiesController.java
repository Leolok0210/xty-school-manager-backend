package com.xiaotiyun.school.manager.controller.student;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.model.entity.StudentEntity;
import com.xiaotiyun.school.manager.model.res.StudentLeisureActivitiesResultResModel;
import com.xiaotiyun.school.manager.service.LeisureActivitiesNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "学生余暇活动")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student/leisure/activities")
public class StudentLeisureActivitiesController extends BasicController {
    private final LeisureActivitiesNoticeService leisureActivitiesNoticeService;

    @ApiOperation("余暇活动匹配通知")
    @GetMapping("/notice")
    public Result<StudentLeisureActivitiesResultResModel> notice(@ApiParam("当前学段id") @RequestParam Long periodId) {
        StudentEntity student = (StudentEntity) StpUtil.getSession().get("student");
        if (student == null) {
            throw new BusinessException(LanguageConstants.SYSTEM_TOKEN_INVALID);
        }
        return Result.success(leisureActivitiesNoticeService.notice(student.getId(), periodId));
    }
}