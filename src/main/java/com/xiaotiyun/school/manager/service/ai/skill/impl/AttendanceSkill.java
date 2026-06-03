package com.xiaotiyun.school.manager.service.ai.skill.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaotiyun.school.manager.model.req.StudentAttendancePageReqModel;
import com.xiaotiyun.school.manager.model.res.StudentAttendancePageResModel;
import com.xiaotiyun.school.manager.service.StudentAttendanceService;
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
public class AttendanceSkill implements AiSkill {

    @Resource
    private StudentAttendanceService studentAttendanceService;

    @Override
    public String getName() {
        return "attendance_check";
    }

    @Override
    public String getDescription() {
        return "查詢學生考勤記錄和統計";
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        properties.put("schoolId", createMap("type", "integer", "description", "學校 ID"));
        properties.put("classId", createMap("type", "integer", "description", "班級 ID"));
        properties.put("studentId", createMap("type", "integer", "description", "學生 ID"));
        properties.put("startDate", createMap("type", "string", "description", "開始日期 YYYY-MM-DD"));
        properties.put("endDate", createMap("type", "string", "description", "結束日期 YYYY-MM-DD"));
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

            Long schoolId = params.get("schoolId") != null ? ((Number) params.get("schoolId")).longValue() : context.getSchoolId();
            Long classId = params.get("classId") != null ? ((Number) params.get("classId")).longValue() : null;
            Long studentId = params.get("studentId") != null ? ((Number) params.get("studentId")).longValue() : null;

            StudentAttendancePageReqModel reqModel = new StudentAttendancePageReqModel();
            reqModel.setUserId(1L);
            reqModel.setSchoolId(schoolId);
            if (classId != null) reqModel.setClassId(classId);
            if (studentId != null) reqModel.setStudentId(studentId);
            if (params.get("startDate") != null) reqModel.setQueryStartDate(java.time.LocalDate.parse(params.get("startDate").toString()));
            if (params.get("endDate") != null) reqModel.setQueryEndDate(java.time.LocalDate.parse(params.get("endDate").toString()));
            reqModel.setPageNum(1);
            reqModel.setPageSize(100);

            PageInfo<StudentAttendancePageResModel> pageInfo = studentAttendanceService.page(reqModel);

            List<Map<String, Object>> cards = new ArrayList<>();
            for (StudentAttendancePageResModel att : pageInfo.getList()) {
                Map<String, Object> card = new HashMap<>();
                card.put("studentName", att.getStudentName() != null ? att.getStudentName() : "");
                card.put("className", att.getClassName() != null ? att.getClassName() : "");
                card.put("date", att.getAttendanceDate() != null ? att.getAttendanceDate().toString() : "");
                card.put("status", att.getStatus() != null ? att.getStatus() : "");
                card.put("remark", att.getRemark() != null ? att.getRemark() : "");
                cards.add(card);
            }

            return SkillResult.ok("查到 " + pageInfo.getTotal() + " 筆記錄", null, cards);
        } catch (Exception e) {
            log.error("attendance_check error", e);
            return SkillResult.fail("查詢考勤失敗：" + e.getMessage());
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