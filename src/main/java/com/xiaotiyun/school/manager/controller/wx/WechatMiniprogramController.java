package com.xiaotiyun.school.manager.controller.wx;

import cn.dev33.satoken.annotation.SaIgnore;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.ChannelBindReq;
import com.xiaotiyun.school.manager.model.res.MinigrogramAuthResModel;
import com.xiaotiyun.school.manager.model.res.WechatMiniprogramUserChannelResModel;
import com.xiaotiyun.school.manager.service.WechatMiniprogramChannelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 小程序渠道表控制器
 */
@RestController
@RequestMapping("/api/wechatMiniprogramChannel")
@Api(tags = "小程序渠道管理")
public class WechatMiniprogramController extends BasicController {

    @Autowired
    private WechatMiniprogramChannelService wechatMiniprogramChannelService;

    @SaIgnore
    @ApiOperation("渠道查询-学生端(非鉴权)")
    @GetMapping("/auth")
    public Result<MinigrogramAuthResModel> authChannel(@ApiParam("用户微信code") @RequestParam String userCode,
                                                       @ApiParam("渠道用户ID") @RequestParam(required = false) Long channelUserId) {
        return wechatMiniprogramChannelService.authChannel(userCode,channelUserId);
    }

//    @SaIgnore
//    @ApiOperation("获取小程序渠道列表")
//    @GetMapping("/list")
//    public Result<List<WechatMiniprogramChannelResModel>> getChannelList() {
//        List<WechatMiniprogramChannelEntity> list = wechatMiniprogramChannelService.list();
//        List<WechatMiniprogramChannelResModel> resModelList = list == null ?
//                new ArrayList<>() : list.stream().map(item -> {
//            WechatMiniprogramChannelResModel resModel = new WechatMiniprogramChannelResModel();
//            resModel.setId(item.getId());
//            resModel.setChannelName(item.getChannelName());
//            resModel.setChannelUrl(item.getChannelUrl());
//            return resModel;
//        }).collect(Collectors.toList());
//        return Result.success(resModelList);
//    }

    @SaIgnore
    @ApiOperation("绑定渠道列表-学生端(非鉴权)")
    @PostMapping("/bind")
    public Result<?> bindChannel(@Valid @RequestBody ChannelBindReq reqModel) {
        return wechatMiniprogramChannelService.bindChannel(reqModel);
    }

    @SaIgnore
    @ApiOperation("切换学生列表-学生端(非鉴权)")
    @GetMapping("/student/switch")
    public Result<List<WechatMiniprogramUserChannelResModel>> switchStudent(@RequestParam String code) {
        return wechatMiniprogramChannelService.switchStudent(code);
    }

    @SaIgnore
    @ApiOperation("用户解绑渠道-学生端(非鉴权)")
    @GetMapping("/unBind")
    public Result<?> unBindChannel(@ApiParam("用户微信code") @RequestParam String code,
                                   @ApiParam("学生ID，需要解绑的渠道的学生ID") @RequestParam Long studentId) {
        wechatMiniprogramChannelService.unBindChannel(code, studentId);
        return Result.success();
    }
}
