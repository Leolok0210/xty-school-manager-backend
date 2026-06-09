package com.xiaotiyun.school.manager.service.ai.skill.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaotiyun.school.manager.model.entity.SysClass;
import com.xiaotiyun.school.manager.service.SysClassService;
import com.xiaotiyun.school.manager.service.ai.skill.AiContext;
import com.xiaotiyun.school.manager.service.ai.skill.AiSkill;
import com.xiaotiyun.school.manager.service.ai.skill.SkillResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component
public class UpdateClassInfoSkill implements AiSkill {

    @Resource
    private SysClassService sysClassService;

    @Override
    public String getName() {
        return "update_class_info";
    }

    @Override
    public String getDescription() {
        return "更新班級資訊（班主任、班級名稱等）";
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        List<String> required = new java.util.ArrayList<>();
        required.add("classId");

        properties.put("classId", createMap("type", "integer", "description", "班級ID（必填）"));
        properties.put("className", createMap("type", "string", "description", "新班級名稱"));
        properties.put("headTeacher", createMap("type", "integer", "description", "班主任ID（user_school_rel ID）"));
        properties.put("department", createMap("type", "integer", "description", "學部"));
        properties.put("gradeGroup", createMap("type", "integer", "description", "級組ID"));
        properties.put("sid", createMap("type", "string", "description", "學年"));
        properties.put("professionalVersion", createMap("type", "integer", "description", "是否專業班：0=否，1=是"));
        properties.put("artsScience", createMap("type", "integer", "description", "文理科/理工商科"));
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

            Long classId = params.get("classId") != null ? ((Number) params.get("classId")).longValue() : null;
            if (classId == null) {
                return SkillResult.fail("請提供要更新的班級ID");
            }

            SysClass sysClass = sysClassService.getSysClassById(classId);
            if (sysClass == null) {
                return SkillResult.fail("找不到班級 (ID: " + classId + ")");
            }

            if (params.get("className") != null) {
                sysClass.setClassName(params.get("className").toString());
            }
            if (params.get("headTeacher") != null) {
                sysClass.setHeadTeacher(((Number) params.get("headTeacher")).longValue());
            }
            if (params.get("department") != null) {
                sysClass.setDepartment(((Number) params.get("department")).intValue());
            }
            if (params.get("gradeGroup") != null) {
                sysClass.setGradeGroup(((Number) params.get("gradeGroup")).longValue());
            }
            if (params.get("sid") != null) {
                sysClass.setSid(params.get("sid").toString());
            }
            if (params.get("professionalVersion") != null) {
                sysClass.setProfessionalVersion(((Number) params.get("professionalVersion")).intValue());
            }
            if (params.get("artsScience") != null) {
                sysClass.setArtsScience(((Number) params.get("artsScience")).intValue());
            }

            sysClassService.updateSysClass(sysClass);

            String className = sysClass.getClassName();
            return SkillResult.ok("成功更新班級「" + className + "」的資訊");

        } catch (Exception e) {
            log.error("update_class_info error", e);
            return SkillResult.fail("更新班級失敗：" + e.getMessage());
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
