package com.xiaotiyun.school.manager.service.ai.skill.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaotiyun.school.manager.model.req.StudentSaveReqModel;
import com.xiaotiyun.school.manager.service.SysClassService;
import com.xiaotiyun.school.manager.service.StudentService;
import com.xiaotiyun.school.manager.service.ai.skill.AiContext;
import com.xiaotiyun.school.manager.service.ai.skill.AiSkill;
import com.xiaotiyun.school.manager.service.ai.skill.SkillResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component
public class AddStudentSkill implements AiSkill {

    @Resource
    private StudentService studentService;

    @Resource
    private SysClassService sysClassService;

    @Override
    public String getName() {
        return "add_student";
    }

    @Override
    public String getDescription() {
        return "新增一名學生到指定班級";
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        List<String> required = new java.util.ArrayList<>();
        required.add("chineseName");

        properties.put("chineseName", createMap("type", "string", "description", "中文姓名（必填）"));
        properties.put("className", createMap("type", "string", "description", "班級名稱，如「中五1班」（與classId二選一）"));
        properties.put("classId", createMap("type", "integer", "description", "班級ID（與className二選一）"));
        properties.put("gender", createMap("type", "integer", "description", "性別：1=男，2=女，默認1"));
        properties.put("studentNo", createMap("type", "string", "description", "學號，不提供則自動生成"));
        properties.put("englishName", createMap("type", "string", "description", "外文姓名"));
        properties.put("birthDate", createMap("type", "string", "description", "出生日期 YYYY-MM-DD"));
        properties.put("mobilePhone", createMap("type", "string", "description", "手提電話"));
        properties.put("schoolYear", createMap("type", "string", "description", "學年，默認從系統獲取"));
        properties.put("status", createMap("type", "integer", "description", "狀態：1=在校（默認），2=畢業，3=退學，4=休學，5=轉學"));
        params.put("properties", properties);
        params.put("required", required);
        return params;
    }

    @Override
    public boolean isAvailableForRole(String role) {
        return true;
    }

    @Override
    public SkillResult execute(Map<String, Object> params, AiContext context) {
        try {
            StpUtil.login(1L);

            Long schoolId = context.getSchoolId() != null ? context.getSchoolId() : 1L;
            String chineseName = params.get("chineseName") != null ? params.get("chineseName").toString() : null;
            if (chineseName == null || chineseName.isEmpty()) {
                return SkillResult.fail("學生姓名不能為空");
            }

            Long classId = params.get("classId") != null ? ((Number) params.get("classId")).longValue() : null;
            if (classId == null && params.get("className") != null) {
                classId = resolveClassId(schoolId, params.get("className").toString());
            }
            if (classId == null) {
                return SkillResult.fail("請提供班級ID或班級名稱");
            }

            Integer gender = params.get("gender") != null ? ((Number) params.get("gender")).intValue() : 1;
            Integer status = params.get("status") != null ? ((Number) params.get("status")).intValue() : 1;
            String studentNo = params.get("studentNo") != null ? params.get("studentNo").toString() : null;
            String schoolYear = params.get("schoolYear") != null ? params.get("schoolYear").toString() : context.getSchoolYear();

            if (studentNo == null || studentNo.isEmpty()) {
                studentNo = "AI" + System.currentTimeMillis() % 100000;
            }

            StudentSaveReqModel reqModel = new StudentSaveReqModel();
            reqModel.setSchoolId(schoolId);
            reqModel.setChineseName(chineseName);
            reqModel.setClassId(classId);
            reqModel.setGender(gender);
            reqModel.setStatus(status);
            reqModel.setStudentNo(studentNo);
            reqModel.setEducationNo(studentNo);
            reqModel.setSchoolYear(schoolYear);

            if (params.get("englishName") != null) {
                reqModel.setEnglishName(params.get("englishName").toString());
            }
            if (params.get("birthDate") != null) {
                reqModel.setBirthDate(java.time.LocalDate.parse(params.get("birthDate").toString()));
            }
            if (params.get("mobilePhone") != null) {
                reqModel.setMobilePhone(params.get("mobilePhone").toString());
            }

            Long newId = studentService.save(reqModel);

            List<Map<String, Object>> cards = new ArrayList<>();
            Map<String, Object> card = new HashMap<>();
            card.put("學生ID", newId);
            card.put("姓名", chineseName);
            card.put("班級ID", classId);
            card.put("學號", studentNo);
            card.put("狀態", "已創建");
            cards.add(card);

            Map<String, Object> data = new HashMap<>();
            data.put("studentId", newId);
            data.put("name", chineseName);

            return SkillResult.ok("成功創建學生「" + chineseName + "」(ID: " + newId + ")", data, cards);
        } catch (Exception e) {
            log.error("add_student error", e);
            return SkillResult.fail("創建學生失敗：" + e.getMessage());
        }
    }

    private Long resolveClassId(Long schoolId, String className) {
        List<?> classes = sysClassService.getSysClassListBySchoolIdAndClassName(schoolId, className);
        if (classes != null && !classes.isEmpty()) {
            try {
                Object c = classes.get(0);
                Object cId = c.getClass().getMethod("getClassId").invoke(c);
                if (cId != null) {
                    return ((Number) cId).longValue();
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    private Map<String, Object> createMap(Object... kvs) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < kvs.length; i += 2) {
            map.put(kvs[i].toString(), kvs[i + 1]);
        }
        return map;
    }
}
