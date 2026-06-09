package com.xiaotiyun.school.manager.service.ai.skill.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaotiyun.school.manager.model.req.StudentSaveReqModel;
import com.xiaotiyun.school.manager.model.res.StudentResModel;
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
public class UpdateStudentSkill implements AiSkill {

    @Resource
    private StudentService studentService;

    @Resource
    private SysClassService sysClassService;

    @Override
    public String getName() {
        return "update_student";
    }

    @Override
    public String getDescription() {
        return "更新學生資訊（提供studentId + 要修改的欄位）";
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        List<String> required = new java.util.ArrayList<>();
        required.add("studentId");

        properties.put("studentId", createMap("type", "integer", "description", "學生ID（必填）"));
        properties.put("chineseName", createMap("type", "string", "description", "中文姓名"));
        properties.put("englishName", createMap("type", "string", "description", "外文姓名"));
        properties.put("gender", createMap("type", "integer", "description", "性別：1=男，2=女"));
        properties.put("classId", createMap("type", "integer", "description", "班級ID"));
        properties.put("className", createMap("type", "string", "description", "班級名稱（用於查找classId）"));
        properties.put("studentNo", createMap("type", "string", "description", "學號"));
        properties.put("mobilePhone", createMap("type", "string", "description", "手提電話"));
        properties.put("birthDate", createMap("type", "string", "description", "出生日期 YYYY-MM-DD"));
        properties.put("status", createMap("type", "integer", "description", "狀態：1=在校，2=畢業，3=退學，4=休學，5=轉學"));
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

            Long studentId = params.get("studentId") != null ? ((Number) params.get("studentId")).longValue() : null;
            if (studentId == null) {
                return SkillResult.fail("請提供要更新的學生ID");
            }

            StudentResModel existing = studentService.info(studentId);
            if (existing == null) {
                return SkillResult.fail("找不到學生 (ID: " + studentId + ")");
            }

            Long classId = params.get("classId") != null ? ((Number) params.get("classId")).longValue() : existing.getClassId();
            if (params.get("className") != null && params.get("classId") == null) {
                Long resolved = resolveClassId(existing.getSchoolId(), params.get("className").toString());
                if (resolved != null) {
                    classId = resolved;
                }
            }

            StudentSaveReqModel reqModel = new StudentSaveReqModel();
            reqModel.setSchoolId(existing.getSchoolId());
            reqModel.setChineseName(params.get("chineseName") != null ? params.get("chineseName").toString() : existing.getChineseName());
            reqModel.setEnglishName(params.get("englishName") != null ? params.get("englishName").toString() : existing.getEnglishName());
            reqModel.setGender(params.get("gender") != null ? ((Number) params.get("gender")).intValue() : existing.getGender());
            reqModel.setClassId(classId);
            reqModel.setStatus(params.get("status") != null ? ((Number) params.get("status")).intValue() : existing.getStatus());
            reqModel.setStudentNo(existing.getStudentNo());
            reqModel.setEducationNo(existing.getEducationNo());
            reqModel.setMobilePhone(params.get("mobilePhone") != null ? params.get("mobilePhone").toString() : existing.getMobilePhone());
            reqModel.setImgUrl(existing.getImgUrl());

            if (params.get("birthDate") != null) {
                reqModel.setBirthDate(java.time.LocalDate.parse(params.get("birthDate").toString()));
            } else {
                reqModel.setBirthDate(existing.getBirthDate());
            }

            studentService.update(studentId, reqModel);

            List<Map<String, Object>> cards = new ArrayList<>();
            Map<String, Object> card = new HashMap<>();
            card.put("學生ID", studentId);
            card.put("姓名", reqModel.getChineseName());
            card.put("狀態", "已更新");
            cards.add(card);

            return SkillResult.ok("成功更新學生「" + reqModel.getChineseName() + "」的資訊", null, cards);
        } catch (Exception e) {
            log.error("update_student error", e);
            return SkillResult.fail("更新學生失敗：" + e.getMessage());
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
