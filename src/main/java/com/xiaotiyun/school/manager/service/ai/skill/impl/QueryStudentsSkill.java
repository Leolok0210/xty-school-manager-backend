package com.xiaotiyun.school.manager.service.ai.skill.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaotiyun.school.manager.model.entity.StudentEntity;
import com.xiaotiyun.school.manager.model.res.StudentPageResModel;
import com.xiaotiyun.school.manager.model.req.StudentPageReqModel;
import com.xiaotiyun.school.manager.service.SysClassService;
import com.xiaotiyun.school.manager.service.StudentService;
import com.xiaotiyun.school.manager.service.ai.skill.AiContext;
import com.xiaotiyun.school.manager.service.ai.skill.AiSkill;
import com.xiaotiyun.school.manager.service.ai.skill.SkillResult;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component
public class QueryStudentsSkill implements AiSkill {

    @Resource
    private StudentService studentService;

    @Resource
    private SysClassService sysClassService;

    @Override
    public String getName() {
        return "query_students";
    }

    @Override
    public String getDescription() {
        return "使用可選條件查詢學生列表";
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        properties.put("classId", createMap("type", "integer", "description", "班級 ID"));
        properties.put("schoolYear", createMap("type", "string", "description", "學年"));
        properties.put("name", createMap("type", "string", "description", "學生姓名（部分匹配）"));
        properties.put("studentNo", createMap("type", "string", "description", "學號"));
        properties.put("className", createMap("type", "string", "description", "班級名稱（可部分匹配）"));
        params.put("properties", properties);
        return params;
    }

    @Override
    public boolean isAvailableForRole(String role) {
        return true;
    }

    @Override
    public SkillResult execute(Map<String, Object> params, AiContext context) {
        try {
            // 建立 AI 管理員登錄 session
            StpUtil.login(1L);

            StudentPageReqModel reqModel = new StudentPageReqModel();
            reqModel.setUserId(1L);
            Long schoolId = context.getSchoolId() != null ? context.getSchoolId() : 1L;
            reqModel.setSchoolId(schoolId);

            // 優先使用 classId，如果沒有則嘗試使用 className 搜尋班級
            if (params.get("classId") != null) {
                reqModel.setClassId(((Number) params.get("classId")).longValue());
            }
            if (params.get("name") != null) {
                reqModel.setStudentInfo(params.get("name").toString());
            }
            if (params.get("studentNo") != null) {
                reqModel.setStudentInfo(params.get("studentNo").toString());
            }
            // 處理 className 參數：如果是「中五1班」這樣的名稱，需要拆分並查找班級
            if (params.get("className") != null && params.get("classId") == null) {
                String className = params.get("className").toString();
                log.info("query_students called with className={}", className);
                // 嘗試拆分班級名稱（如「中五1班」拆為「中五」和「1班」）
                int splitIndex = -1;
                for (int i = 1; i < className.length(); i++) {
                    char ch = className.charAt(i);
                    if (ch >= '0' && ch <= '9') {
                        splitIndex = i - 1;
                        break;
                    }
                }
                if (splitIndex > 0) {
                    String gradePart = className.substring(0, splitIndex + 1);
                    String classPart = className.substring(splitIndex + 1);
                    // 使用 service 查找匹配的班級
                    List<?> classes = sysClassService.getSysClassListBySchoolIdAndClassName(schoolId, gradePart);
                    // 找到包含 classPart 的班級
                    for (Object c : classes) {
                        Object cName = null;
                        try {
                            cName = c.getClass().getMethod("getClassName").invoke(c);
                        } catch (Exception ignored) {}
                        if (cName != null && cName.toString().contains(classPart)) {
                            try {
                                Object cId = c.getClass().getMethod("getClassId").invoke(c);
                                if (cId != null) {
                                    reqModel.setClassId(((Number) cId).longValue());
                                    log.info("Found classId={} for className={}", reqModel.getClassId(), className);
                                    break;
                                }
                            } catch (Exception ignored) {}
                        }
                    }
                }
            }
            reqModel.setPageNum(1);
            reqModel.setPageSize(50);

            PageInfo<StudentPageResModel> pageInfo = studentService.page(reqModel);

            List<Map<String, Object>> cards = new ArrayList<>();
            for (StudentPageResModel s : pageInfo.getList()) {
                Map<String, Object> card = new HashMap<>();
                // 不顯示 id 和 classId 等內部字段
                card.put("姓名", s.getStudentName() != null ? s.getStudentName() : "");
                card.put("性別", s.getGender() != null ? (s.getGender() == 1 ? "男" : "女") : "");
                card.put("學號", s.getStudentNo() != null ? s.getStudentNo() : "");
                cards.add(card);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("total", pageInfo.getTotal());
            data.put("students", cards);

            String classInfo = "";
            if (params.get("className") != null) {
                classInfo = "（「" + params.get("className").toString() + "」）";
            } else if (reqModel.getClassId() != null) {
                classInfo = "（班級ID: " + reqModel.getClassId() + "）";
            }
            return SkillResult.ok("查到 " + pageInfo.getTotal() + " 名學生" + classInfo, data, cards);
        } catch (Exception e) {
            log.error("query_students error", e);
            return SkillResult.fail("查詢學生失敗：" + e.getMessage());
        }
    }

    private Map<String, Object> createMap(Object... kvs) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < kvs.length; i += 2) {
            map.put(kvs[i].toString(), kvs[i + 1]);
        }
        return map;
    }
}