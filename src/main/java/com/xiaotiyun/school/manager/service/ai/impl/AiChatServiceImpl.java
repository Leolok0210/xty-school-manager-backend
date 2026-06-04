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
            // 優先使用請求中的schoolId，否則從session獲取
            Long schoolId = reqModel.getSchoolId() != null ? reqModel.getSchoolId() : getCurrentSchoolId();

            // 生成會話ID（如果沒有）
            if (reqModel.getSessionId() == null || reqModel.getSessionId().isEmpty()) {
                reqModel.setSessionId(UUID.randomUUID().toString().replace("-", ""));
            }

            // 嘗試保存用戶消息（如果數據庫表不存在則跳過）
            try {
                saveUserMessage(reqModel);
            } catch (Exception e) {
                log.debug("Failed to save user message, skipping (table may not exist): {}", e.getMessage());
            }

            // 构建请求
            JSONObject qwenRequest = buildQwenRequest(reqModel, schoolId);
            String response = callQwenApiWithRetry(qwenRequest, 3);

            log.info("Qwen API response: {}", response);
            JSONObject qwenResponse = JSON.parseObject(response);
            AiChatResModel resModel = parseQwenResponse(qwenResponse, reqModel);

            // 嘗試保存AI回复（如果數據庫表不存在則跳過）
            try {
                aiChatSessionService.saveMessage(reqModel.getSessionId(), "assistant", resModel.getContent());
            } catch (Exception e) {
                log.debug("Failed to save assistant message, skipping (table may not exist): {}", e.getMessage());
            }

            // 觸發學習（記錄對話到學習系統）
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

                        // 更新用戶習慣（即使是匿名用戶也追蹤）
                        try {
                            if (userId == null || userId <= 0) {
                                userId = 0L; // 匿名用戶
                            }
                            userHabitService.updateHabit(userId, schoolId, lastMsg.getContent());
                        } catch (Exception e) {
                            log.debug("Failed to update user habit, skipping: {}", e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                log.debug("Failed to learn from conversation, skipping: {}", e.getMessage());
            }

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
        AiChatResModel resModel = new AiChatResModel();
        try {
            AiContext context = AiContext.of(getCurrentUserId(), getCurrentSchoolId(), "teacher");

            AiSkill skill = skillRegistry.get(actionId);
            if (skill == null) {
                resModel.setContent("未知操作：" + actionId);
                return resModel;
            }

            // 記錄操作審計日誌
            log.info("AI executeAction - userId: {}, schoolId: {}, action: {}, params: {}",
                getCurrentUserId(), context.getSchoolId(), actionId, params);

            JSONObject arguments = JSON.parseObject(JSON.toJSONString(params));
            SkillResult result = skill.execute(arguments, context);

            StringBuilder sb = new StringBuilder();
            if (result.getMessage() != null && !result.getMessage().isEmpty()) {
                sb.append(result.getMessage());
            } else {
                sb.append("操作已執行完成");
            }

            if (result.getDataCards() != null && !result.getDataCards().isEmpty()) {
                List<AiChatResModel.DataCard> cards = convertToDataCards(result.getDataCards(), actionId);
                resModel.setDataCards(cards);
            }

            resModel.setContent(sb.toString());
        } catch (Exception e) {
            log.error("executeAction error", e);
            resModel.setContent("操作執行失敗：" + e.getMessage());
        }
        return resModel;
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

    private JSONObject buildQwenRequest(AiChatReqModel reqModel, Long schoolId) {
        JSONObject request = new JSONObject();

        // 构建知识库上下文（如果数据库表不存在则返回空）
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

        JSONArray messages = new JSONArray();

        JSONObject systemMsg = new JSONObject();
        systemMsg.put("role", "system");
        systemMsg.put("content", fullSystemPrompt);
        messages.add(systemMsg);

        // 添加历史消息（如果数据库表不存在则跳过）
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

        request.put("model", aiConfig.getModel());
        request.put("messages", messages);
        request.put("tools", JSON.parseArray(JSON.toJSONString(skillRegistry.getToolDefinitions())));
        request.put("tool_choice", "auto");

        return request;
    }

    private AiChatResModel parseQwenResponse(JSONObject qwenResponse, AiChatReqModel reqModel) {
        AiChatResModel resModel = new AiChatResModel();

        JSONArray choices = qwenResponse.getJSONArray("choices");
        if (choices != null && !choices.isEmpty()) {
            JSONObject message = choices.getJSONObject(0).getJSONObject("message");
            String content = message.getString("content");
            resModel.setContent(content != null ? content : "");

            JSONArray toolCalls = message.getJSONArray("tool_calls");
            if (toolCalls != null && !toolCalls.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append(content != null ? content : "");

                for (int i = 0; i < toolCalls.size(); i++) {
                    JSONObject toolCall = toolCalls.getJSONObject(i);
                    JSONObject function = toolCall.getJSONObject("function");
                    String functionName = function.getString("name");
                    JSONObject arguments = JSON.parseObject(function.getString("arguments"));

                    AiContext context = AiContext.of(getCurrentUserId(), getCurrentSchoolId(), "teacher");
                    // 如果 schoolId 為 0，使用預設值 1
                    if (context.getSchoolId() == null || context.getSchoolId() == 0L) {
                        log.warn("schoolId unavailable for user {}, falling back to 0 (no data)", context.getUserId());
                        context.setSchoolId(0L);
                    }

                    // 如果調用了 query_classes 並帶有 className，自動查詢該班學生
                    if ("query_classes".equals(functionName)) {
                        String className = arguments.getString("className");
                        AiSkill classSkill = skillRegistry.get("query_classes");
                        if (classSkill != null) {
                            SkillResult classResult = classSkill.execute(arguments, context);
                            if (classResult.getMessage() != null && !classResult.getMessage().isEmpty()) {
                                sb.append("\n\n").append(classResult.getMessage());
                            }
                            if (classResult.getDataCards() != null && !classResult.getDataCards().isEmpty()) {
                                List<AiChatResModel.DataCard> cards = convertToDataCards(classResult.getDataCards(), functionName);
                                resModel.setDataCards(cards);
                            }
                        }
                        // 自動查詢該班學生
                        if (className != null && !className.isEmpty()) {
                            AiSkill studentSkill = skillRegistry.get("query_students");
                            if (studentSkill != null) {
                                JSONObject studentArgs = new JSONObject();
                                studentArgs.put("className", className);
                                studentArgs.put("schoolId", context.getSchoolId());
                                SkillResult studentResult = studentSkill.execute(studentArgs, context);
                                if (studentResult.getMessage() != null && !studentResult.getMessage().isEmpty()) {
                                    sb.append("\n\n").append(studentResult.getMessage());
                                }
                                if (studentResult.getDataCards() != null && !studentResult.getDataCards().isEmpty()) {
                                    List<AiChatResModel.DataCard> studentCards = convertToDataCards(studentResult.getDataCards(), "query_students");
                                    if (resModel.getDataCards() == null) {
                                        resModel.setDataCards(studentCards);
                                    } else {
                                        resModel.getDataCards().addAll(studentCards);
                                    }
                                }
                                // 檢查用戶是否詢問成績，如果是則自動查詢成績
                                boolean isGradeQuery = isGradeRelatedQuery(reqModel);
                                if (isGradeQuery) {
                                    // 從學生結果中提取學生ID
                                    List<Long> studentIds = extractStudentIdsFromResult(studentResult);
                                    if (!studentIds.isEmpty()) {
                                        AiSkill gradeSkill = skillRegistry.get("check_semester_grades");
                                        if (gradeSkill != null) {
                                            for (Long studentId : studentIds) {
                                                JSONObject gradeArgs = new JSONObject();
                                                gradeArgs.put("studentId", studentId);
                                                SkillResult gradeResult = gradeSkill.execute(gradeArgs, context);
                                                if (gradeResult.getMessage() != null && !gradeResult.getMessage().isEmpty()) {
                                                    sb.append("\n\n").append(gradeResult.getMessage());
                                                }
                                                if (gradeResult.getDataCards() != null && !gradeResult.getDataCards().isEmpty()) {
                                                    List<AiChatResModel.DataCard> gradeCards = convertToDataCards(gradeResult.getDataCards(), "check_semester_grades");
                                                    if (resModel.getDataCards() == null) {
                                                        resModel.setDataCards(gradeCards);
                                                    } else {
                                                        resModel.getDataCards().addAll(gradeCards);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        resModel.setContent(sb.toString());
                        continue;
                    }

                    if (functionName.startsWith("add_") || functionName.startsWith("update_") || functionName.startsWith("delete_") || functionName.startsWith("register_") || functionName.startsWith("import_")) {
                        resModel.setRequiresConfirmation(true);
                        resModel.setActionType("execute");
                        resModel.setActionDescription("即將執行: " + functionName);

                        Map<String, Object> pendingAction = new HashMap<>();
                        pendingAction.put("functionName", functionName);
                        pendingAction.put("arguments", arguments);
                        resModel.setPendingAction(pendingAction);
                    } else {
                        AiSkill skill = skillRegistry.get(functionName);
                        if (skill != null) {
                            SkillResult result = skill.execute(arguments, context);
                            // 只顯示格式化消息，不輸出原始 JSON 數據
                            if (result.getMessage() != null && !result.getMessage().isEmpty()) {
                                sb.append("\n\n").append(result.getMessage());
                            }

                            if (result.getDataCards() != null && !result.getDataCards().isEmpty()) {
                                List<AiChatResModel.DataCard> cards = convertToDataCards(result.getDataCards(), functionName);
                                resModel.setDataCards(cards);
                            }

                            // 如果是 query_students 且用戶詢問成績，自動查詢成績
                            if ("query_students".equals(functionName) && isGradeRelatedQuery(reqModel)) {
                                List<Long> studentIds = extractStudentIdsFromResult(result);
                                if (!studentIds.isEmpty()) {
                                    for (Long studentId : studentIds) {
                                        // 查詢學期成績
                                        AiSkill gradeSkill = skillRegistry.get("check_semester_grades");
                                        if (gradeSkill != null) {
                                            JSONObject gradeArgs = new JSONObject();
                                            gradeArgs.put("studentId", studentId);
                                            SkillResult gradeResult = gradeSkill.execute(gradeArgs, context);
                                            if (gradeResult.getMessage() != null && !gradeResult.getMessage().isEmpty()) {
                                                sb.append("\n\n").append(gradeResult.getMessage());
                                            }
                                            if (gradeResult.getDataCards() != null && !gradeResult.getDataCards().isEmpty()) {
                                                List<AiChatResModel.DataCard> gradeCards = convertToDataCards(gradeResult.getDataCards(), "check_semester_grades");
                                                if (resModel.getDataCards() == null) {
                                                    resModel.setDataCards(gradeCards);
                                                } else {
                                                    resModel.getDataCards().addAll(gradeCards);
                                                }
                                            }
                                        }
                                        // 查詢日常成績
                                        AiSkill dailySkill = skillRegistry.get("query_daily_grades");
                                        if (dailySkill != null) {
                                            JSONObject dailyArgs = new JSONObject();
                                            dailyArgs.put("studentId", studentId);
                                            SkillResult dailyResult = dailySkill.execute(dailyArgs, context);
                                            if (dailyResult.getMessage() != null && !dailyResult.getMessage().isEmpty()) {
                                                sb.append("\n\n").append(dailyResult.getMessage());
                                            }
                                            if (dailyResult.getDataCards() != null && !dailyResult.getDataCards().isEmpty()) {
                                                List<AiChatResModel.DataCard> dailyCards = convertToDataCards(dailyResult.getDataCards(), "query_daily_grades");
                                                if (resModel.getDataCards() == null) {
                                                    resModel.setDataCards(dailyCards);
                                                } else {
                                                    resModel.getDataCards().addAll(dailyCards);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            // 移除原始 JSON 輸出，避免洩露內部數據
                        } else {
                            // 未知技能，只顯示提示訊息，不顯示技能名稱
                            sb.append("\n\n抱歉，暫時無法處理這個請求。");
                        }
                    }
                }
                resModel.setContent(sb.toString());
            }
        }

        resModel.setSessionId(reqModel.getSessionId());
        return resModel;
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

    private boolean isGradeRelatedQuery(AiChatReqModel reqModel) {
        if (reqModel.getMessages() == null || reqModel.getMessages().isEmpty()) {
            return false;
        }
        // 檢查最後一條用戶消息是否包含成績相關關鍵詞
        for (int i = reqModel.getMessages().size() - 1; i >= 0; i--) {
            AiChatReqModel.ChatMessage msg = reqModel.getMessages().get(i);
            if ("user".equals(msg.getRole())) {
                String content = msg.getContent().toLowerCase();
                return content.contains("成績") || content.contains("分數") ||
                       content.contains("物理") || content.contains("數學") ||
                       content.contains("英文") || content.contains("中文") ||
                       content.contains("化學") || content.contains("生物") ||
                       content.contains("歷史") || content.contains("地理") ||
                       content.contains("科目") || content.contains("所有") ||
                       content.contains("查詢");
            }
        }
        return false;
    }

    private List<Long> extractStudentIdsFromResult(SkillResult studentResult) {
        List<Long> studentIds = new ArrayList<>();
        if (studentResult.getDataCards() != null) {
            for (Map<String, Object> card : studentResult.getDataCards()) {
                Object idObj = card.get("id");
                if (idObj != null) {
                    if (idObj instanceof Long) {
                        studentIds.add((Long) idObj);
                    } else if (idObj instanceof Integer) {
                        studentIds.add(((Integer) idObj).longValue());
                    } else if (idObj instanceof String) {
                        try {
                            studentIds.add(Long.parseLong((String) idObj));
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
        }
        return studentIds;
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