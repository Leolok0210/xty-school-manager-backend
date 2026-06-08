package com.xiaotiyun.school.manager.service.ai.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaotiyun.school.manager.config.AiConfig;
import com.xiaotiyun.school.manager.model.entity.AiChatMessageEntity;
import com.xiaotiyun.school.manager.model.entity.AiChatSessionEntity;
import com.xiaotiyun.school.manager.model.entity.AiKnowledgeBaseEntity;
import com.xiaotiyun.school.manager.model.req.ai.AiChatReqModel;
import com.xiaotiyun.school.manager.model.res.ai.AiChatResModel;
import com.xiaotiyun.school.manager.service.ai.AiChatService;
import com.xiaotiyun.school.manager.service.ai.skill.AiContext;
import com.xiaotiyun.school.manager.service.ai.skill.AiSkill;
import com.xiaotiyun.school.manager.service.ai.skill.AiSkillRegistry;
import com.xiaotiyun.school.manager.service.ai.skill.SkillResult;
import com.xiaotiyun.school.manager.service.AiChatSessionService;
import com.xiaotiyun.school.manager.service.AiKnowledgeBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AiChatServiceImpl implements AiChatService {

    @Resource
    private AiConfig aiConfig;

    @Resource
    private AiSkillRegistry skillRegistry;

    @Resource
    private AiChatSessionService aiChatSessionService;

    @Resource
    private AiKnowledgeBaseService aiKnowledgeBaseService;

    @Resource
    private com.xiaotiyun.school.manager.service.SchoolService schoolService;

    @Resource
    private com.xiaotiyun.school.manager.service.AiLearningService aiLearningService;

    @Resource
    private com.xiaotiyun.school.manager.service.UserHabitService userHabitService;

    private String systemPrompt;

    private final Map<String, AgentLoopState> pendingAgentStates = new ConcurrentHashMap<>();

    private static final int MAX_AGENT_ITERATIONS = 10;

    /**
     * Holds the state of an in-progress agent loop that was paused for write confirmation.
     */
    private static class AgentLoopState {
        private List<JSONObject> messages;
        private String pendingFunctionName;
        private Map<String, Object> pendingArguments;
        private String toolCallId;
        private Long schoolId;
        private String sessionId;
        private AiChatResModel accumulatedResponse;
    }

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("ai-system-prompt.txt");
            try (InputStream is = resource.getInputStream()) {
                systemPrompt = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            log.error("Failed to load ai-system-prompt.txt, using default", e);
            systemPrompt = "你是學校管理系統的 AI 助手";
        }
    }

    @Override
    public AiChatResModel chat(AiChatReqModel reqModel) {
        try {
            Long userId = getCurrentUserId();
            Long schoolId = reqModel.getSchoolId() != null ? reqModel.getSchoolId() : getCurrentSchoolId();

            // 生成會話ID（如果沒有）
            if (reqModel.getSessionId() == null || reqModel.getSessionId().isEmpty()) {
                reqModel.setSessionId(UUID.randomUUID().toString().replace("-", ""));
            }

            // 清理该会话的旧 pending state（用户发了新消息，旧的确认就过期了）
            pendingAgentStates.remove(reqModel.getSessionId());

            // 嘗試保存用戶消息（如果數據庫表不存在則跳過）
            try {
                saveUserMessage(reqModel);
            } catch (Exception e) {
                log.debug("Failed to save user message, skipping (table may not exist): {}", e.getMessage());
            }

            // 構建初始消息列表
            List<JSONObject> messages = buildInitialMessages(reqModel, schoolId);

            // 執行 Agent Loop
            AiChatResModel resModel = runAgentLoop(messages, schoolId, reqModel.getSessionId());

            // 只有非確認狀態才保存 assistant 消息和觸發學習
            if (!Boolean.TRUE.equals(resModel.getRequiresConfirmation())) {
                try {
                    if (resModel.getContent() != null && !resModel.getContent().isEmpty()) {
                        aiChatSessionService.saveMessage(reqModel.getSessionId(), "assistant", resModel.getContent());
                    }
                } catch (Exception e) {
                    log.debug("Failed to save assistant message, skipping (table may not exist): {}", e.getMessage());
                }

                // 觸發學習
                try {
                    if (reqModel.getMessages() != null && !reqModel.getMessages().isEmpty()) {
                        List<AiChatReqModel.ChatMessage> msgs = reqModel.getMessages();
                        AiChatReqModel.ChatMessage lastMsg = msgs.get(msgs.size() - 1);
                        if ("user".equals(lastMsg.getRole())) {
                            aiLearningService.learnFromConversation(
                                reqModel.getSessionId(),
                                lastMsg.getContent(),
                                resModel.getContent(),
                                schoolId
                            );

                            try {
                                Long uid = (userId == null || userId <= 0) ? 0L : userId;
                                userHabitService.updateHabit(uid, schoolId, lastMsg.getContent());
                            } catch (Exception e) {
                                log.debug("Failed to update user habit, skipping: {}", e.getMessage());
                            }
                        }
                    }
                } catch (Exception e) {
                    log.debug("Failed to learn from conversation, skipping: {}", e.getMessage());
                }
            }

            resModel.setSessionId(reqModel.getSessionId());
            return resModel;
        } catch (Exception e) {
            log.error("AI chat error", e);
            AiChatResModel resModel = new AiChatResModel();
            resModel.setContent("抱歉，發生了錯誤：" + e.getMessage());
            return resModel;
        }
    }

    @Override
    public AiChatResModel executeAction(String sessionId, String actionId, Object params) {
        try {
            AiContext context = AiContext.of(getCurrentUserId(), getCurrentSchoolId(), "teacher");

            AiSkill skill = skillRegistry.get(actionId);
            if (skill == null) {
                AiChatResModel res = new AiChatResModel();
                res.setContent("未知操作：" + actionId);
                return res;
            }

            log.info("AI executeAction - userId: {}, schoolId: {}, action: {}, params: {}",
                getCurrentUserId(), context.getSchoolId(), actionId, params);

            JSONObject arguments = JSON.parseObject(JSON.toJSONString(params));
            SkillResult result = skill.execute(arguments, context);

            // 檢查是否有暫停的 Agent Loop 需要恢復
            AgentLoopState pendingState = pendingAgentStates.remove(sessionId);
            if (pendingState != null) {
                log.info("Resuming agent loop after confirmation for session {}", sessionId);

                List<JSONObject> messages = pendingState.messages;

                // 重建 assistant message（含 tool_calls）
                JSONObject assistantMsg = new JSONObject();
                assistantMsg.put("role", "assistant");
                assistantMsg.put("content", (String) null);
                JSONArray toolCalls = new JSONArray();
                JSONObject tc = new JSONObject();
                tc.put("id", pendingState.toolCallId);
                tc.put("type", "function");
                JSONObject func = new JSONObject();
                func.put("name", pendingState.pendingFunctionName);
                func.put("arguments", JSON.toJSONString(pendingState.pendingArguments));
                tc.put("function", func);
                toolCalls.add(tc);
                assistantMsg.put("tool_calls", toolCalls);
                messages.add(assistantMsg);

                // 添加 tool result（已確認執行的結果）
                String toolContent = result.getMessage() != null ? result.getMessage() : "操作已執行完成";
                messages.add(buildToolResultMessage(pendingState.toolCallId, toolContent));

                // 累積 data cards
                AiChatResModel accumulated = pendingState.accumulatedResponse;
                if (result.getDataCards() != null && !result.getDataCards().isEmpty()) {
                    List<AiChatResModel.DataCard> cards = convertToDataCards(result.getDataCards(), actionId);
                    if (accumulated.getDataCards() == null) {
                        accumulated.setDataCards(new ArrayList<>());
                    }
                    accumulated.getDataCards().addAll(cards);
                }
                // 清除確認狀態
                accumulated.setRequiresConfirmation(false);
                accumulated.setActionType(null);
                accumulated.setActionDescription(null);
                accumulated.setPendingAction(null);

                // 恢復 Agent Loop
                return runAgentLoop(messages, pendingState.schoolId, sessionId);
            }

            // 無 pending state — 獨立執行（legacy 行為）
            StringBuilder sb = new StringBuilder();
            if (result.getMessage() != null && !result.getMessage().isEmpty()) {
                sb.append(result.getMessage());
            } else {
                sb.append("操作已執行完成");
            }

            AiChatResModel resModel = new AiChatResModel();
            if (result.getDataCards() != null && !result.getDataCards().isEmpty()) {
                List<AiChatResModel.DataCard> cards = convertToDataCards(result.getDataCards(), actionId);
                resModel.setDataCards(cards);
            }
            resModel.setContent(sb.toString());
            return resModel;
        } catch (Exception e) {
            log.error("executeAction error", e);
            AiChatResModel resModel = new AiChatResModel();
            resModel.setContent("操作執行失敗：" + e.getMessage());
            return resModel;
        }
    }

    @Override
    public List<Map<String, Object>> getSessionHistory(String sessionId) {
        List<AiChatMessageEntity> messages = aiChatSessionService.getSessionMessages(sessionId);
        return messages.stream().map(msg -> {
            Map<String, Object> map = new HashMap<>();
            map.put("role", msg.getRole());
            map.put("content", msg.getContent());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getUserSessions() {
        Long userId = getCurrentUserId();
        List<AiChatSessionEntity> sessions = aiChatSessionService.getUserSessions(userId);
        return sessions.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("sessionId", s.getSessionId());
            map.put("title", s.getTitle());
            map.put("createTime", s.getCreateTime());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public String createSession() {
        Long userId = getCurrentUserId();
        Long schoolId = getCurrentSchoolId();
        return aiChatSessionService.createSession(userId, schoolId);
    }

    @Override
    public void deleteSession(String sessionId) {
        aiChatSessionService.deleteSession(sessionId);
    }

    @Override
    public void feedback(String sessionId, String messageId, String comment) {
        try {
            aiChatSessionService.updateMessageFeedback(sessionId, messageId, comment);
        } catch (Exception e) {
            log.error("feedback error", e);
            throw new RuntimeException("提交反馈失败：" + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getFaqs() {
        Long schoolId = getCurrentSchoolId();
        List<AiKnowledgeBaseEntity> faqs = aiKnowledgeBaseService.getFaqs(schoolId);
        return faqs.stream().map(faq -> {
            Map<String, Object> map = new HashMap<>();
            map.put("question", faq.getQuestion());
            map.put("answer", faq.getAnswer());
            return map;
        }).collect(Collectors.toList());
    }

    private void saveUserMessage(AiChatReqModel reqModel) {
        if (reqModel.getMessages() != null && !reqModel.getMessages().isEmpty()) {
            AiChatReqModel.ChatMessage lastMsg = reqModel.getMessages().get(reqModel.getMessages().size() - 1);
            if ("user".equals(lastMsg.getRole())) {
                aiChatSessionService.saveMessage(reqModel.getSessionId(), "user", lastMsg.getContent());
            }
        }
    }

    private Long getCurrentUserId() {
        try {
            return StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            return 0L;
        }
    }

    private Long getCurrentSchoolId() {
        try {
            Object schoolIdObj = StpUtil.getSession().get("schoolId");
            if (schoolIdObj != null) {
                return Long.valueOf(schoolIdObj.toString());
            }
        } catch (Exception e) {
            log.warn("Failed to get schoolId from session", e);
        }
        return null;
    }

    private List<JSONObject> buildInitialMessages(AiChatReqModel reqModel, Long schoolId) {
        // 构建知识库上下文
        String knowledgeContext = "";
        try {
            knowledgeContext = aiKnowledgeBaseService.getKnowledgeContext(schoolId);
        } catch (Exception e) {
            log.debug("Failed to load knowledge context, using empty (table may not exist): {}", e.getMessage());
        }

        // 获取学校名称
        String schoolName = "";
        try {
            if (schoolId != null && schoolId > 0) {
                com.xiaotiyun.school.manager.model.entity.SchoolEntity school = schoolService.getById(schoolId);
                if (school != null) {
                    schoolName = school.getName();
                }
            }
        } catch (Exception e) {
            log.debug("Failed to get school name: {}", e.getMessage());
        }

        String schoolContext = schoolName.isEmpty() ? "" : "【當前學校】" + schoolName + "\n\n";

        // 獲取用戶習慣上下文
        String userHabitContext = "";
        try {
            Long userId = getCurrentUserId();
            if (userId != null && userId > 0) {
                List<String> frequentClasses = userHabitService.getFrequentClasses(userId);
                if (frequentClasses != null && !frequentClasses.isEmpty()) {
                    userHabitContext = "【用戶常用班級】" + String.join("、", frequentClasses) + "\n";
                }
            }
        } catch (Exception e) {
            log.debug("Failed to load user habit context: {}", e.getMessage());
        }

        String fullSystemPrompt = systemPrompt + "\n\n" + schoolContext + userHabitContext + knowledgeContext;

        List<JSONObject> messages = new ArrayList<>();

        JSONObject systemMsg = new JSONObject();
        systemMsg.put("role", "system");
        systemMsg.put("content", fullSystemPrompt);
        messages.add(systemMsg);

        // 添加历史消息（只加载 user 和 assistant，不加载 tool 消息）
        if (reqModel.getSessionId() != null && !reqModel.getSessionId().isEmpty()) {
            try {
                List<AiChatMessageEntity> history = aiChatSessionService.getSessionMessages(reqModel.getSessionId());
                for (AiChatMessageEntity msg : history) {
                    JSONObject msgObj = new JSONObject();
                    msgObj.put("role", msg.getRole());
                    msgObj.put("content", msg.getContent());
                    messages.add(msgObj);
                }
            } catch (Exception e) {
                log.debug("Failed to load session history, skipping (table may not exist): {}", e.getMessage());
            }
        }

        // 添加当前消息
        if (reqModel.getMessages() != null) {
            for (AiChatReqModel.ChatMessage msg : reqModel.getMessages()) {
                JSONObject msgObj = new JSONObject();
                msgObj.put("role", msg.getRole());
                msgObj.put("content", msg.getContent());
                messages.add(msgObj);
            }
        }

        return messages;
    }

    private JSONObject buildAgentRequest(List<JSONObject> messages) {
        JSONObject request = new JSONObject();
        request.put("model", aiConfig.getModel());
        request.put("messages", new com.alibaba.fastjson.JSONArray(messages));
        request.put("tools", JSON.parseArray(JSON.toJSONString(skillRegistry.getToolDefinitions())));
        request.put("tool_choice", "auto");
        return request;
    }

    private JSONObject buildToolResultMessage(String toolCallId, String content) {
        JSONObject msg = new JSONObject();
        msg.put("role", "tool");
        msg.put("tool_call_id", toolCallId);
        msg.put("content", content);
        return msg;
    }

    private JSONObject buildToolErrorMessage(String toolCallId, String error) {
        JSONObject msg = new JSONObject();
        msg.put("role", "tool");
        msg.put("tool_call_id", toolCallId);
        msg.put("content", "Error: " + error);
        return msg;
    }

    private AiChatResModel runAgentLoop(List<JSONObject> messages, Long schoolId, String sessionId) {
        AiChatResModel accumulated = new AiChatResModel();
        accumulated.setContent("");

        int iteration = 0;
        while (iteration < MAX_AGENT_ITERATIONS) {
            iteration++;
            log.info("Agent loop iteration {}/{} for session {}", iteration, MAX_AGENT_ITERATIONS, sessionId);

            // 1. 構建請求並調用 API
            JSONObject request = buildAgentRequest(messages);
            String responseBody;
            try {
                responseBody = callQwenApiWithRetry(request, 3);
            } catch (Exception e) {
                log.error("API call failed on iteration {}: {}", iteration, e.getMessage());
                accumulated.setContent("抱歉，AI 服務暫時不可用，請稍後再試。");
                pendingAgentStates.remove(sessionId);
                return accumulated;
            }

            log.info("Agent API response (iteration {}): {}", iteration, responseBody);
            JSONObject qwenResponse = JSON.parseObject(responseBody);
            JSONArray choices = qwenResponse.getJSONArray("choices");
            if (choices == null || choices.isEmpty()) {
                log.error("Empty choices in API response, iteration {}", iteration);
                accumulated.setContent("抱歉，AI 返回了空結果，請重新提問。");
                pendingAgentStates.remove(sessionId);
                return accumulated;
            }

            JSONObject choice = choices.getJSONObject(0);
            JSONObject message = choice.getJSONObject("message");
            String content = message.getString("content");
            JSONArray toolCalls = message.getJSONArray("tool_calls");

            // 2. 沒有 tool_calls → 最終答案
            if (toolCalls == null || toolCalls.isEmpty()) {
                if (content != null && !content.isEmpty()) {
                    accumulated.setContent(content);
                } else if (accumulated.getContent().isEmpty()) {
                    accumulated.setContent("抱歉，我無法處理這個請求。");
                }
                pendingAgentStates.remove(sessionId);
                return accumulated;
            }

            // 3. 把 assistant message（含 tool_calls）加到消息列表
            messages.add(message);

            // 4. 執行每個 tool call
            for (int i = 0; i < toolCalls.size(); i++) {
                JSONObject toolCall = toolCalls.getJSONObject(i);
                String toolCallId = toolCall.getString("id");
                JSONObject function = toolCall.getJSONObject("function");
                String functionName = function.getString("name");
                JSONObject arguments;
                try {
                    arguments = JSON.parseObject(function.getString("arguments"));
                } catch (Exception e) {
                    log.warn("Failed to parse tool arguments for {}: {}", functionName, e.getMessage());
                    arguments = new JSONObject();
                }

                AiSkill skill = skillRegistry.get(functionName);
                if (skill == null) {
                    messages.add(buildToolErrorMessage(toolCallId, "找不到工具: " + functionName));
                    continue;
                }

                // 4a. 檢查是否需要確認（寫入操作）
                boolean needsConfirm = skill.requiresConfirmation()
                    || functionName.startsWith("add_")
                    || functionName.startsWith("update_")
                    || functionName.startsWith("delete_")
                    || functionName.startsWith("register_")
                    || functionName.startsWith("import_");

                if (needsConfirm) {
                    String preview = skill.getConfirmationPreview(arguments,
                        AiContext.of(getCurrentUserId(), schoolId, "teacher"));
                    if (preview == null || preview.isEmpty()) {
                        preview = "即將執行: " + functionName;
                    }

                    accumulated.setRequiresConfirmation(true);
                    accumulated.setActionType("execute");
                    accumulated.setActionDescription(preview);

                    Map<String, Object> pendingAction = new HashMap<>();
                    pendingAction.put("functionName", functionName);
                    pendingAction.put("arguments", arguments);
                    accumulated.setPendingAction(pendingAction);

                    // 存儲 agent loop state 以便確認後恢復
                    AgentLoopState state = new AgentLoopState();
                    state.messages = new ArrayList<>(messages);
                    // 移除剛加入的 assistant message（確認後會重建）
                    state.messages.remove(state.messages.size() - 1);
                    state.pendingFunctionName = functionName;
                    state.pendingArguments = arguments;
                    state.toolCallId = toolCallId;
                    state.schoolId = schoolId;
                    state.sessionId = sessionId;
                    state.accumulatedResponse = accumulated;
                    pendingAgentStates.put(sessionId, state);

                    log.info("Agent loop paused for confirmation: {} in session {}", functionName, sessionId);
                    return accumulated;
                }

                // 4b. 執行 skill
                AiContext context = AiContext.of(getCurrentUserId(), schoolId, "teacher");
                if (context.getSchoolId() == null || context.getSchoolId() == 0L) {
                    context.setSchoolId(0L);
                }

                SkillResult result = skill.execute(arguments, context);

                // 4c. 累積 data cards
                if (result.getDataCards() != null && !result.getDataCards().isEmpty()) {
                    List<AiChatResModel.DataCard> cards = convertToDataCards(result.getDataCards(), functionName);
                    if (accumulated.getDataCards() == null) {
                        accumulated.setDataCards(new ArrayList<>());
                    }
                    accumulated.getDataCards().addAll(cards);
                }

                // 4d. 添加 tool result 消息
                String toolContent = result.getMessage() != null ? result.getMessage()
                    : (result.isSuccess() ? "執行成功" : "執行失敗");
                messages.add(buildToolResultMessage(toolCallId, toolContent));
            }
        }

        // 超過最大迭代次數
        log.warn("Agent loop exceeded max iterations ({}) for session {}", MAX_AGENT_ITERATIONS, sessionId);
        pendingAgentStates.remove(sessionId);
        if (accumulated.getContent().isEmpty()) {
            accumulated.setContent("抱歉，處理過程需要較多步驟，請簡化您的問題後重新提問。");
        }
        return accumulated;
    }

    private List<AiChatResModel.DataCard> convertToDataCards(List<Map<String, Object>> dataCards, String skillName) {
        if (dataCards == null || dataCards.isEmpty()) {
            return new ArrayList<>();
        }

        AiChatResModel.DataCard card = new AiChatResModel.DataCard();
        card.setType("table");
        card.setTitle(getCardTitle(skillName));

        AiChatResModel.DataCard.Payload payload = new AiChatResModel.DataCard.Payload();
        List<String> columns = new ArrayList<>();
        List<List<Object>> rows = new ArrayList<>();

        Map<String, Object> firstItem = dataCards.get(0);
        for (Map.Entry<String, Object> entry : firstItem.entrySet()) {
            columns.add(entry.getKey());
        }

        for (Map<String, Object> item : dataCards) {
            List<Object> row = new ArrayList<>();
            for (String col : columns) {
                row.add(item.get(col));
            }
            rows.add(row);
        }

        payload.setColumns(columns);
        payload.setRows(rows);
        card.setPayload(payload);

        List<AiChatResModel.DataCard> result = new ArrayList<>();
        result.add(card);
        return result;
    }

    private String getCardTitle(String skillName) {
        switch (skillName) {
            case "query_classes": return "班級列表";
            case "query_students": return "學生列表";
            case "query_daily_grades": return "日常成績";
            case "check_semester_grades": return "學期考試成績";
            case "conduct_check": return "獎懲記錄";
            case "attendance_check": return "考勤記錄";
            default: return "查詢結果";
        }
    }

    private String callQwenApiWithRetry(JSONObject qwenRequest, int maxRetries) throws Exception {
        int attempt = 0;
        Exception lastException = null;

        while (attempt < maxRetries) {
            try {
                return HttpUtil.createPost(aiConfig.getApiUrl())
                        .header("Authorization", "Bearer " + aiConfig.getApiKey())
                        .header("Content-Type", "application/json")
                        .timeout(aiConfig.getTimeout() != null ? aiConfig.getTimeout() : 30000)
                        .body(qwenRequest.toJSONString())
                        .execute()
                        .body();
            } catch (Exception e) {
                lastException = e;
                attempt++;
                log.warn("Qwen API call failed (attempt {}/{}): {}", attempt, maxRetries, e.getMessage());
                if (attempt < maxRetries) {
                    // Exponential backoff: 1s, 2s, 4s...
                    long sleepTime = (long) Math.pow(2, attempt - 1) * 1000;
                    Thread.sleep(sleepTime);
                }
            }
        }

        throw lastException != null ? lastException : new Exception("Qwen API call failed after " + maxRetries + " attempts");
    }
}