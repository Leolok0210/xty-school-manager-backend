package com.xiaotiyun.school.manager.service.ai.skill.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaotiyun.school.manager.model.res.StudentListResModel;
import com.xiaotiyun.school.manager.model.res.StudentLateCountResModel;
import com.xiaotiyun.school.manager.model.res.StudentLeaveStatisticsResModel;
import com.xiaotiyun.school.manager.service.StudentAttendanceService;
import com.xiaotiyun.school.manager.service.StudentLeaveService;
import com.xiaotiyun.school.manager.service.StudentService;
import com.xiaotiyun.school.manager.service.ai.skill.AiContext;
import com.xiaotiyun.school.manager.service.ai.skill.AiSkill;
import com.xiaotiyun.school.manager.service.ai.skill.SkillResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CheckAttendanceAlertsSkill implements AiSkill {

    @Resource
    private StudentAttendanceService studentAttendanceService;

    @Resource
    private StudentLeaveService studentLeaveService;

    @Resource
    private StudentService studentService;

    @Override
    public String getName() {
        return "check_attendance_alerts";
    }

    @Override
    public String getDescription() {
        return "檢查班級考勤異常，找出遲到或請假過多的學生";
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        List<String> required = new java.util.ArrayList<>();
        required.add("classId");

        properties.put("classId", createMap("type", "integer", "description", "班級ID（必填）"));
        properties.put("lateThreshold", createMap("type", "integer", "description", "遲到次數閾值，默認3"));
        properties.put("leaveThreshold", createMap("type", "integer", "description", "請假節數閾值，默認10"));
        properties.put("schoolYear", createMap("type", "string", "description", "學年，默認從系統獲取"));
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

            Long classId = ((Number) params.get("classId")).longValue();
            Long schoolId = context.getSchoolId() != null ? context.getSchoolId() : 1L;
            String schoolYear = params.get("schoolYear") != null ? params.get("schoolYear").toString() : context.getSchoolYear();
            int lateThreshold = params.get("lateThreshold") != null ? ((Number) params.get("lateThreshold")).intValue() : 3;
            int leaveThreshold = params.get("leaveThreshold") != null ? ((Number) params.get("leaveThreshold")).intValue() : 10;

            // Get all students in class
            List<StudentListResModel> students = studentService.listByClassId(classId);
            if (students == null || students.isEmpty()) {
                return SkillResult.fail("該班級沒有學生");
            }

            List<Long> studentIds = students.stream().map(StudentListResModel::getId).collect(Collectors.toList());
            Map<Long, String> nameMap = students.stream()
                .collect(Collectors.toMap(StudentListResModel::getId, s -> s.getChineseName() != null ? s.getChineseName() : ""));

            // Get late counts
            List<StudentLateCountResModel> lateCounts = studentAttendanceService.getStudentLateCount(schoolId, classId, studentIds);
            Map<Long, Integer> lateMap = new HashMap<>();
            if (lateCounts != null) {
                for (StudentLateCountResModel lc : lateCounts) {
                    lateMap.put(lc.getStudentId(), lc.getLateCount() != null ? lc.getLateCount() : 0);
                }
            }

            // Get leave statistics
            List<StudentLeaveStatisticsResModel> leaveStats = studentLeaveService.getStudentLeaveStatistics(schoolId, classId, studentIds);
            Map<Long, Integer> leaveMap = new HashMap<>();
            if (leaveStats != null) {
                for (StudentLeaveStatisticsResModel ls : leaveStats) {
                    leaveMap.merge(ls.getStudentId(), ls.getTotalPeriods() != null ? ls.getTotalPeriods() : 0, Integer::sum);
                }
            }

            // Build alert list
            List<Map<String, Object>> cards = new ArrayList<>();
            int alertCount = 0;

            for (Long sid : studentIds) {
                int late = lateMap.getOrDefault(sid, 0);
                int leave = leaveMap.getOrDefault(sid, 0);

                if (late >= lateThreshold || leave >= leaveThreshold) {
                    alertCount++;
                    String status = "";
                    if (late >= lateThreshold) status += "遲到" + late + "次 ";
                    if (leave >= leaveThreshold) status += "請假/缺席" + leave + "節";

                    Map<String, Object> card = new HashMap<>();
                    card.put("學生ID", sid);
                    card.put("姓名", nameMap.getOrDefault(sid, "未知"));
                    card.put("遲到次數", late);
                    card.put("請假缺席節數", leave);
                    card.put("狀態", "⚠ " + status.trim());
                    cards.add(card);
                }
            }

            if (cards.isEmpty()) {
                return SkillResult.ok("班級考勤狀況良好，沒有發現異常學生（遲到閾值: " + lateThreshold + "次，請假閾值: " + leaveThreshold + "節）", null, new ArrayList<>());
            }

            Map<String, Object> data = new HashMap<>();
            data.put("alertCount", alertCount);
            data.put("totalStudents", students.size());

            return SkillResult.ok("發現 " + alertCount + " 名學生考勤異常（共 " + students.size() + " 名學生）", data, cards);

        } catch (Exception e) {
            log.error("check_attendance_alerts error", e);
            return SkillResult.fail("考勤異常檢查失敗：" + e.getMessage());
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
