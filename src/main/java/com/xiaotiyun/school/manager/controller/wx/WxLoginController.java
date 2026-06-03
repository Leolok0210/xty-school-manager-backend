package com.xiaotiyun.school.manager.controller.wx;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.exception.AesException;
import com.xiaotiyun.school.manager.model.req.WXAppRegisterReq;
import com.xiaotiyun.school.manager.model.req.WXBindAndLoginReq;
import com.xiaotiyun.school.manager.model.req.WXStuBindAndLoginReq;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.EnterpriseWechatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Slf4j
@Api(tags = "企业微信相关接口")
@RestController
@RequestMapping("/api/wx")
public class WxLoginController extends BasicController {

    @Resource
    private EnterpriseWechatService enterpriseWechatService;

    @SaIgnore
    @ApiOperation("账号绑定-教师")
    @PostMapping("/wxbind")
    public Result<LoginResModel> wxLoginTea(@RequestBody @Valid WXBindAndLoginReq reqModel) {
        return Result.success(enterpriseWechatService.bindAndLogin(reqModel));
    }

    @SaIgnore
    @ApiOperation("企业三方微信回调用户Code-教师")
    @GetMapping("/receiveUserCode")
    public Result<String> wxReceiveTea(@RequestParam String code, HttpServletResponse response) throws IOException {
        enterpriseWechatService.getUserInfo(code, true, response);
        return Result.success();
    }

    @SaIgnore
    @ApiOperation("企业内部微信回调用户Code-教师")
    @GetMapping("/internal/receiveUserCode")
    public Result<String> wxReceiveTeaInternal(@RequestParam String code, @RequestParam String state, HttpServletResponse response) throws IOException {
        enterpriseWechatService.getUserInfoInternal(code, state, response);
        return Result.success();
    }

    @SaIgnore
    @ApiOperation("企业微信回调suiteToken-教师")
    @PostMapping ("/suite/receiveToken")
    public String suiteReceivePostTea(HttpServletRequest request,
                                   @RequestParam(value = "msg_signature") String msgSignature,
                                   @RequestParam(value = "timestamp") String timestamp,
                                   @RequestParam(value = "nonce") String nonce,
                                   @RequestBody String requestBody) {
        // 异步线程执行方法，因为企微要求1000ms内返回success
        new Thread(() -> {
            try {
                enterpriseWechatService.saveSuiteTicket(request, msgSignature, timestamp, nonce, requestBody, true);
            } catch (AesException e) {
                log.error("请求内容: {}",requestBody);
                log.error("企业微信回调suiteToken-教师异常:{}", e.getMessage());
            }
        }).start();
        // 该接口返回success
        return "success";
    }

    @ApiOperation("检查企业ID授权信息")
    @GetMapping ("/checkCorpId")
    public Result<SchoolWeixinRelevanceCheckResModel> checkCorpId(@RequestParam String id,
                                                                  @ApiParam(value = "类型,1-内部应用,2-三方应用", required = true) @RequestParam Integer type) {
        return enterpriseWechatService.checkCorpId(id, getSchoolId(), type);
    }

    @ApiOperation("绑定企业ID")
    @GetMapping ("/bindCorpId/{id}")
    public Result<SchoolWeixinRelevanceCheckResModel> bindCorpId(@PathVariable String id) {
        return enterpriseWechatService.bindCorpId(id, getSchoolId());
    }

    @SaIgnore
    @ApiOperation("企微应用注册验证-教师")
    @GetMapping ("/suite/receiveToken")
    public String applicationRegistrationTea(WXAppRegisterReq reqModel) throws AesException {
        return enterpriseWechatService.getApplicationRegistration(reqModel,true);
    }

    @SaIgnore
    @ApiOperation("账号学生绑定-学生端(非鉴权)")
    @PostMapping("/student/wxbind")
    public Result<LoginResModel> wxLoginStu(@RequestBody @Valid WXStuBindAndLoginReq reqModel) {
        return Result.success(enterpriseWechatService.stuBindAndLogin(reqModel));
    }

    @SaIgnore
    @ApiOperation("企业微信回调用户Code-学生")
    @GetMapping("/student/receiveUserCode")
    public Result<String> wxReceiveStu(@RequestParam String code, HttpServletResponse response) throws IOException {
        enterpriseWechatService.getUserInfo(code, false, response);
        return Result.success();
    }
    @SaIgnore
    @ApiOperation("小程序回调用户Code-学生")
    @GetMapping("/student/mini/receiveUserCode")
    public Result<MinigrogramUserResModel> wxMiniReceiveStu(@ApiParam("wx.login返回的code") @RequestParam String code,
                                                            @ApiParam("学生ID") @RequestParam Long studentId){
        log.info("接收到学生用户code:{}", code);
        MinigrogramUserResModel userInfo = enterpriseWechatService.getUserInfo(code, studentId);
        return Result.success(userInfo);
    }

    @SaIgnore
    @ApiOperation("企业微信回调suiteToken-学生")
    @PostMapping ("/student/suite/receiveToken")
    public String suiteReceivePostStu(HttpServletRequest request,
                                   @RequestParam(value = "msg_signature") String msgSignature,
                                   @RequestParam(value = "timestamp") String timestamp,
                                   @RequestParam(value = "nonce") String nonce,
                                   @RequestBody String requestBody) throws AesException {
        enterpriseWechatService.saveSuiteTicket(request, msgSignature, timestamp, nonce, requestBody, false);
        // 该接口返回success
        return "success";
    }

    @SaIgnore
    @ApiOperation("企微应用注册验证-学生")
    @GetMapping ("/student/suite/receiveToken")
    public String applicationRegistrationStu(WXAppRegisterReq reqModel) throws AesException {
        return enterpriseWechatService.getApplicationRegistration(reqModel,false);
    }

    @SaCheckPermission("wx:student:unbind")
    @ApiOperation("小程序解绑-学生")
    @GetMapping("/student/unbind/{studentId}")
    public Result<String> studentUnbind(@PathVariable Long studentId) {
        return Result.success(enterpriseWechatService.studentUnbind(studentId, getSchoolId()));
    }

    @ApiOperation("小程序解绑-学生端(非鉴权)")
    @GetMapping("/mini/student/unbind/{studentId}")
    public Result<String> studentMiniUnbind(@PathVariable Long studentId) {
        return Result.success(enterpriseWechatService.studentUnbind(studentId, getSchoolId()));
    }

    @ApiOperation("企微查询绑定-学生")
    @GetMapping("/student/getBind/{studentId}")
    public Result<String> studentGetBind(@PathVariable Long studentId) {
        return Result.success(enterpriseWechatService.studentGetBind(studentId));
    }


//    @ApiOperation("切换学生查询-学生端")
//    @GetMapping("/student/switchSchool")
//    public Result<List<StudentWeCharSchoolResModel>> switchSchool(){
//        return Result.success(enterpriseWechatService.switchSchool());
//    }


//    @ApiOperation("切换学校学生-学生端")
//    @GetMapping("/student/switchSchoolStu")
//    public Result<String> switchSchoolStu(@RequestParam Long studentId){
//        return Result.success(enterpriseWechatService.changeWeCharStudentSchool(studentId));
//    }
}
