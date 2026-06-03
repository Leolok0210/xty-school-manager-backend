package com.xiaotiyun.school.manager.controller.wx;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.EnterpriseWechatNoticePageReqModel;
import com.xiaotiyun.school.manager.model.req.EnterpriseWechatNoticeSaveReqModel;
import com.xiaotiyun.school.manager.model.req.EnterpriseWechatNoticeUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.EnterpriseWechatNoticeResModel;
import com.xiaotiyun.school.manager.service.EnterpriseWechatNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 企业微信通知控制器
 */
@RestController
@Api(tags = "企业微信通知管理")
@RequiredArgsConstructor
@RequestMapping("/api/enterprise/wechat/notice")
public class EnterpriseWechatNoticeController extends BasicController {
    private final EnterpriseWechatNoticeService enterpriseWechatNoticeService;

    @GetMapping("/page")
    @ApiOperation("分页查询企业微信通知列表")
    @SaCheckPermission("enterprise:wechat:notice:page")
    public Result<PageInfo<EnterpriseWechatNoticeResModel>> page(@Valid EnterpriseWechatNoticePageReqModel reqModel) {
        return Result.success(enterpriseWechatNoticeService.page(getSchoolId(), reqModel));
    }

    @PostMapping
    @ApiOperation("创建企业微信通知")
    @SaCheckPermission("enterprise:wechat:notice:create")
    public Result<Long> create(@Valid @RequestBody EnterpriseWechatNoticeSaveReqModel reqModel) {
        return Result.success(enterpriseWechatNoticeService.create(getSchoolId(), getUserId(), reqModel));
    }

    @PutMapping("/{id}")
    @ApiOperation("修改企业微信通知")
    @SaCheckPermission("enterprise:wechat:notice:update")
    public Result<Void> update(
            @PathVariable Long id,
            @Valid @RequestBody EnterpriseWechatNoticeUpdateReqModel reqModel) {
        enterpriseWechatNoticeService.update(id, reqModel);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除企业微信通知")
    @SaCheckPermission("enterprise:wechat:notice:delete")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(enterpriseWechatNoticeService.delete(id));
    }
}