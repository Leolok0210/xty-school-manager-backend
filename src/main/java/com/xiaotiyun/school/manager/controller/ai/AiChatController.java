package com.xiaotiyun.school.manager.controller.ai;

import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.ai.AiChatReqModel;
import com.xiaotiyun.school.manager.model.res.ai.AiChatResModel;
import com.xiaotiyun.school.manager.service.ai.AiChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Api(tags = "AI聊天")
@RestController
@RequestMapping("/api/ai")
public class AiChatController extends BasicController {

    @Resource
    private AiChatService aiChatService;

    @ApiOperation("AI聊天")
    @PostMapping("/chat")
    public Result<AiChatResModel> chat(@Valid @RequestBody AiChatReqModel reqModel) {
        AiChatResModel response = aiChatService.chat(reqModel);
        return Result.success(response);
    }

    @ApiOperation("执行AI确认的操作")
    @PostMapping("/execute")
    public Result<AiChatResModel> executeAction(
            @RequestParam String sessionId,
            @RequestParam String actionId,
            @RequestBody Object params) {
        AiChatResModel response = aiChatService.executeAction(sessionId, actionId, params);
        return Result.success(response);
    }

    @ApiOperation("获取会话历史")
    @GetMapping("/history/{sessionId}")
    public Result<List<Map<String, Object>>> getSessionHistory(@PathVariable String sessionId) {
        List<Map<String, Object>> history = aiChatService.getSessionHistory(sessionId);
        return Result.success(history);
    }

    @ApiOperation("获取用户会话列表")
    @GetMapping("/sessions")
    public Result<List<Map<String, Object>>> getUserSessions() {
        List<Map<String, Object>> sessions = aiChatService.getUserSessions();
        return Result.success(sessions);
    }

    @ApiOperation("创建新会话")
    @PostMapping("/session")
    public Result<String> createSession() {
        String sessionId = aiChatService.createSession();
        return Result.success(sessionId);
    }

    @ApiOperation("删除会话")
    @DeleteMapping("/session/{sessionId}")
    public Result<Void> deleteSession(@PathVariable String sessionId) {
        aiChatService.deleteSession(sessionId);
        return Result.success(null);
    }

    @ApiOperation("获取FAQ列表")
    @GetMapping("/faqs")
    public Result<List<Map<String, Object>>> getFaqs() {
        List<Map<String, Object>> faqs = aiChatService.getFaqs();
        return Result.success(faqs);
    }

    @ApiOperation("提交消息反馈")
    @PostMapping("/feedback")
    public Result<Void> feedback(
            @RequestParam String sessionId,
            @RequestParam String messageId,
            @RequestParam String comment) {
        aiChatService.feedback(sessionId, messageId, comment);
        return Result.success(null);
    }
}