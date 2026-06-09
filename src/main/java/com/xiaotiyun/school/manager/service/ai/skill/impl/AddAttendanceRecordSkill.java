package com.xiaotiyun.school.manager.service.ai.skill.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaotiyun.school.manager.model.req.StudentLeaveSaveAdminReqModel;
import com.xiaotiyun.school.manager.service.StudentLeaveService;
import com.xiaotiyun.school.manager.service.ai.skill.AiContext;
import com.xiaotiyun.school.manager.service.ai.skill.AiSkill;
import com.xiaotiyun.school.manager.service.ai.skill.SkillResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class AddAttendanceRecordSkill implements AiSkill {

    @Resource
    private StudentLeaveService studentLeaveService;

    @Override
    public String getName() {
        return "add_attendance_record";
    }

    @Override
    public String getDescription() {
        return "記錄學生請假/缺席/遲到（可批量）";
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        List<String> required = new java.util.ArrayList<>();
        required.add("classId");
        required.add("studentIds");
        required.add("leaveDate");
        required.add("leaveType");
        required.add("periods");

        properties.put("classId", createMap("type", "integer", "description", "班級ID（必填）"));
        Map<String, Object> idsArray = new HashMap<>();
        idsArray.put("type", "array");
        idsArray.put("items", createMap("type", "integer"));
        idsArray.put("description", "學生ID列表");
        properties.put("studentIds", idsArray);
        properties.put("leaveDate", createMap("type", "string", "description", "日期 YYYY-MM-DD（必填）"));
        properties.put("leaveType", createMap("type", "integer", "description", "類型：1=請假，2=缺席，3=遲到（必填）"));
        properties.put("periods", createMap("type", "integer", "description", "節數（必填）"));
        properties.put("remark", createMap("type", "string", "description", "備註"));
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

            Long schoolId = context.getSchoolId() != null ? context.getSchoolId() : 1L;
            String schoolYear = params.get("schoolYear") != null ? params.get("schoolYear").toString() : context.getSchoolYear();
            Long classId = ((Number) params.get("classId")).longValue();
            LocalDate leaveDate = LocalDate.parse(params.get("leaveDate").toString());
            int leaveType = ((Number) params.get("leaveType")).intValue();
            int periods = ((Number) params.get("periods")).intValue();

            @SuppressWarnings("unchecked")
            List<Number> rawIds = (List<Number>) params.get("studentIds");
            List<Long> studentIds = new ArrayList<>();
            for (Number n : rawIds) {
                studentIds.add(n.longValue());
            }

            StudentLeaveSaveAdminReqModel reqModel = new StudentLeaveSaveAdminReqModel();
            reqModel.setSchoolId(schoolId);
            reqModel.setSchoolYear(schoolYear);
            reqModel.setClassId(classId);
            reqModel.setStudentIds(studentIds);
            reqModel.setLeaveDate(leaveDate);
            reqModel.setLeaveType(leaveType);
            reqModel.setPeriods(periods);

            if (params.get("remark") != null) {
                reqModel.setRemark(params.get("remark").toString());
            }

            studentLeaveService.save(reqModel);

            String typeName = leaveType == 1 ? "請假" : (leaveType == 2 ? "缺席" : "遲到");
            return SkillResult.ok("成功為 " + studentIds.size() + " 名學生記錄" + typeName + " (" + leaveDate + ", " + periods + "節)");

        } catch (Exception e) {
            log.error("add_attendance_record error", e);
            return SkillResult.fail("考勤記錄失敗：" + e.getMessage());
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
