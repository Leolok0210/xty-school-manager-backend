package com.xiaotiyun.school.manager.service.ai.skill.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaotiyun.school.manager.model.req.StudentPerformanceTotalReqModel;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.*;
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
public class StudentOverviewSkill implements AiSkill {

    @Resource
    private StudentService studentService;

    @Resource
    private StudentLeaveService studentLeaveService;

    @Resource
    private UserRewardService userRewardService;

    @Resource
    private ConventionalPerformanceService conventionalPerformanceService;

    @Resource
    private SysClassService sysClassService;

    @Override
    public String getName() {
        return "student_overview";
    }

    @Override
    public String getDescription() {
        return "查看學生綜合概覽（基本資料+成績+考勤+獎懲）";
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        List<String> required = new java.util.ArrayList<>();
        required.add("studentId");

        properties.put("studentId", createMap("type", "integer", "description", "學生ID（必填）"));
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

            Long studentId = ((Number) params.get("studentId")).longValue();
            String schoolYear = context.getSchoolYear();

            StudentResModel student = studentService.info(studentId);
            if (student == null) {
                return SkillResult.fail("找不到學生 (ID: " + studentId + ")");
            }

            Long schoolId = student.getSchoolId() != null ? student.getSchoolId() : 1L;
            Long classId = student.getClassId();

            List<Map<String, Object>> cards = new ArrayList<>();
            StringBuilder summary = new StringBuilder();
            summary.append("## ").append(student.getChineseName()).append(" 綜合概覽\n\n");

            // 1. 基本資料
            summary.append("### 基本資料\n");
            summary.append("- 姓名: ").append(student.getChineseName() != null ? student.getChineseName() : "-");
            if (student.getEnglishName() != null) summary.append(" (").append(student.getEnglishName()).append(")");
            summary.append("\n");
            summary.append("- 性別: ").append(student.getGender() != null ? (student.getGender() == 1 ? "男" : "女") : "-").append("\n");
            summary.append("- 學號: ").append(student.getStudentNo() != null ? student.getStudentNo() : "-").append("\n");
            summary.append("- 班級: ").append(student.getClassName() != null ? student.getClassName() : "-").append("\n");
            summary.append("- 狀態: ").append(getStatusName(student.getStatus())).append("\n");
            if (student.getMobilePhone() != null) summary.append("- 電話: ").append(student.getMobilePhone()).append("\n");

            // 2. 考勤概覽
            summary.append("\n### 考勤概覽\n");
            try {
                if (classId != null) {
                    List<Long> sidList = Collections.singletonList(studentId);
                    List<StudentLeaveStatisticsResModel> leaveStats = studentLeaveService.getStudentLeaveStatistics(schoolId, classId, sidList);
                    int totalLeave = 0;
                    if (leaveStats != null) {
                        for (StudentLeaveStatisticsResModel ls : leaveStats) {
                            totalLeave += ls.getTotalPeriods() != null ? ls.getTotalPeriods() : 0;
                        }
                    }
                    summary.append("- 請假/缺席總節數: ").append(totalLeave).append("節\n");
                }
            } catch (Exception e) {
                summary.append("- 考勤數據暫時無法獲取\n");
            }

            // 3. 獎懲概覽
            summary.append("\n### 獎懲概覽\n");
            try {
                List<UserRewardCountResModel> rewardCounts = userRewardService.getUserRewardCount(null);
                int rewardTotal = 0;
                if (rewardCounts != null) {
                    for (UserRewardCountResModel rc : rewardCounts) {
                        if (studentId.equals(rc.getStudentId())) {
                            int r = 0;
                            r += rc.getMinRewardCount() != null ? rc.getMinRewardCount() : 0;
                            r += rc.getMidRewardCount() != null ? rc.getMidRewardCount() : 0;
                            r += rc.getMaxRewardCount() != null ? rc.getMaxRewardCount() : 0;
                            rewardTotal += r;
                        }
                    }
                    summary.append("- 獎勵總數: ").append(rewardTotal).append(" 次\n");
                } else {
                    summary.append("- 暫無獎勵記錄\n");
                }
            } catch (Exception e) {
                summary.append("- 獎懲數據暫時無法獲取\n");
            }

            // 4. 常規表現
            summary.append("\n### 常規表現\n");
            try {
                StudentPerformanceTotalReqModel perfReq = new StudentPerformanceTotalReqModel();
                perfReq.setSchoolId(schoolId);
                perfReq.setStudentId(studentId);
                List<StudentPerformanceTotalResModel> perfTotals = conventionalPerformanceService.getTotal(perfReq);
                if (perfTotals != null && !perfTotals.isEmpty()) {
                    for (StudentPerformanceTotalResModel pt : perfTotals) {
                        summary.append("- ").append(getPerformanceTypeName(pt.getType()))
                               .append(": ").append(pt.getNum()).append("次\n");
                    }
                } else {
                    summary.append("- 暫無常規表現記錄\n");
                }
            } catch (Exception e) {
                summary.append("- 表現數據暫時無法獲取\n");
            }

            // Build overview card
            Map<String, Object> card = new HashMap<>();
            card.put("姓名", student.getChineseName() != null ? student.getChineseName() : "");
            card.put("性別", student.getGender() != null ? (student.getGender() == 1 ? "男" : "女") : "");
            card.put("學號", student.getStudentNo() != null ? student.getStudentNo() : "");
            card.put("班級", student.getClassName() != null ? student.getClassName() : "");
            card.put("狀態", getStatusName(student.getStatus()));
            cards.add(card);

            return SkillResult.ok(summary.toString().trim(), null, cards);

        } catch (Exception e) {
            log.error("student_overview error", e);
            return SkillResult.fail("獲取學生概覽失敗：" + e.getMessage());
        }
    }

    private String getStatusName(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 1: return "在校";
            case 2: return "畢業";
            case 3: return "退學";
            case 4: return "休學";
            case 5: return "轉學";
            default: return "未知";
        }
    }

    private String getPerformanceTypeName(int type) {
        switch (type) {
            case 1: return "上課違規";
            case 2: return "欠作業";
            case 3: return "儀表不符";
            case 4: return "遲到";
            case 5: return "欠課本";
            case 6: return "缺席";
            case 7: return "請假";
            case 8: return "優點";
            case 9: return "大功";
            case 10: return "小功";
            case 11: return "缺點";
            case 12: return "大過";
            case 13: return "小過";
            default: return "類型" + type;
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
