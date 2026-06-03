package com.xiaotiyun.school.manager.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionCategoryReqModel;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionCategorySaveReqModel;
import com.xiaotiyun.school.manager.model.res.ExternalCompetitionCategoryResModel;
import com.xiaotiyun.school.manager.service.ExternalCompetitionCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 校外活动范畴Controller
 */
@RestController
@RequestMapping("/api/externalCompetitionCategory")
@Api(tags = "校外活动范畴管理")
public class ExternalCompetitionCategoryController extends BasicController {

    @Autowired
    private ExternalCompetitionCategoryService externalCompetitionCategoryService;

    /**
     * 分页查询校外活动范畴列表
     */
    @GetMapping("/pageList")
    @ApiOperation("分页查询校外活动范畴列表")
    @SaCheckPermission("externalCompetitionCategory:list")
    public Result<PageInfo<ExternalCompetitionCategoryResModel>> pageList(@Validated ExternalCompetitionCategoryReqModel reqModel) {
        Long schoolId = getSchoolId();
        PageInfo<ExternalCompetitionCategoryResModel> pageInfo = externalCompetitionCategoryService.pageList(reqModel, schoolId);
        return Result.success(pageInfo);
    }

    /**
     * 新增或修改校外活动范畴
     */
    @PostMapping("/addOrUpdate")
    @ApiOperation("新增或修改校外活动范畴")
    @SaCheckPermission("externalCompetitionCategory:add")
    public Result<Boolean> addOrUpdate(@Validated @RequestBody List<ExternalCompetitionCategorySaveReqModel> reqModels) {
        Long schoolId = getSchoolId();
        return externalCompetitionCategoryService.addOrUpdate(reqModels, schoolId);
    }

    /**
     * 删除校外活动范畴
     */
    @DeleteMapping("/delete")
    @ApiOperation("删除校外活动范畴")
    @SaCheckPermission("externalCompetitionCategory:delete")
    public Result<Boolean> delete(@RequestParam List<Long> ids) {
        Long schoolId = getSchoolId();
        return externalCompetitionCategoryService.delete(ids, schoolId);
    }

    /**
     * 检查范畴名称是否存在
     */
    @GetMapping("/checkCategoryNameExists")
    @SaCheckPermission("externalCompetitionCategory:check")
    @ApiOperation("检查范畴名称是否存在")
    public Result<Boolean> checkCategoryNameExists(@RequestParam String categoryName, @RequestParam(required = false) Long id) {
        Long schoolId = getSchoolId();
        boolean exists = externalCompetitionCategoryService.checkCategoryNameExists(categoryName, schoolId, id);
        return Result.success(exists);
    }
}
