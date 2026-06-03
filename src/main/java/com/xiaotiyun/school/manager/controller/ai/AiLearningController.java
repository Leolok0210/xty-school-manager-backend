package com.xiaotiyun.school.manager.controller.ai;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.service.AiLearningService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "AI学习管理")
@RestController
@RequestMapping("/api/ai/learn")
public class AiLearningController extends BasicController {

    @Resource
    private AiLearningService aiLearningService;

    @ApiOperation("获取学习统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Long schoolId = getCurrentSchoolId();
        Map<String, Object> stats = aiLearningService.getStats(schoolId);
        return Result.success(stats);
    }

    @ApiOperation("获取建议FAQ")
    @GetMapping("/suggestions")
    public Result<List<Map<String, Object>>> getSuggestions() {
        Long schoolId = getCurrentSchoolId();
        List<Map<String, Object>> suggestions = aiLearningService.getSuggestions(schoolId);
        return Result.success(suggestions);
    }

    @ApiOperation("批准建议并添加到知识库")
    @PostMapping("/approve/{id}")
    public Result<Void> approveSuggestion(@PathVariable Long id) {
        aiLearningService.approveSuggestion(id);
        return Result.success(null);
    }

    @ApiOperation("忽略建议")
    @PostMapping("/ignore/{id}")
    public Result<Void> ignoreSuggestion(@PathVariable Long id) {
        aiLearningService.ignoreSuggestion(id);
        return Result.success(null);
    }

    private Long getCurrentSchoolId() {
        try {
            Object schoolIdObj = StpUtil.getSession().get("schoolId");
            if (schoolIdObj != null) {
                return Long.valueOf(schoolIdObj.toString());
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }
}