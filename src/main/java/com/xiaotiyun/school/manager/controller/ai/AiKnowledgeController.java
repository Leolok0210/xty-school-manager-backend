package com.xiaotiyun.school.manager.controller.ai;

import cn.dev33.satoken.stp.StpUtil;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.entity.AiKnowledgeBaseEntity;
import com.xiaotiyun.school.manager.service.AiKnowledgeBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "AI知识库管理")
@RestController
@RequestMapping("/api/ai/knowledge")
public class AiKnowledgeController extends BasicController {

    @Resource
    private AiKnowledgeBaseService aiKnowledgeBaseService;

    @ApiOperation("获取知识库列表")
    @GetMapping
    public Result<List<Map<String, Object>>> getKnowledgeList(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        Long schoolId = getCurrentSchoolId();
        List<AiKnowledgeBaseEntity> list;
        if (keyword != null && !keyword.isEmpty()) {
            list = aiKnowledgeBaseService.search(schoolId, keyword);
        } else {
            list = aiKnowledgeBaseService.list(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiKnowledgeBaseEntity>()
                    .and(w -> w
                        .isNull(AiKnowledgeBaseEntity::getSchoolId)
                        .or()
                        .eq(AiKnowledgeBaseEntity::getSchoolId, schoolId)
                    )
                    .eq(category != null, AiKnowledgeBaseEntity::getCategory, category)
                    .orderByAsc(AiKnowledgeBaseEntity::getSort)
            );
        }
        return Result.success(list.stream().map(this::toMap).toList());
    }

    @ApiOperation("新增知识库条目")
    @PostMapping
    public Result<Void> addKnowledge(
            @RequestParam String category,
            @RequestParam String question,
            @RequestParam String answer) {
        Long schoolId = getCurrentSchoolId();
        AiKnowledgeBaseEntity entity = new AiKnowledgeBaseEntity();
        entity.setSchoolId(schoolId);
        entity.setCategory(category);
        entity.setQuestion(question);
        entity.setAnswer(answer);
        entity.setStatus(1);
        aiKnowledgeBaseService.save(entity);
        return Result.success(null);
    }

    @ApiOperation("更新知识库条目")
    @PutMapping("/{id}")
    public Result<Void> updateKnowledge(
            @PathVariable Long id,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String question,
            @RequestParam(required = false) String answer) {
        AiKnowledgeBaseEntity entity = aiKnowledgeBaseService.getById(id);
        if (entity == null) {
            return Result.failed(1, "知识条目不存在");
        }
        if (category != null) entity.setCategory(category);
        if (question != null) entity.setQuestion(question);
        if (answer != null) entity.setAnswer(answer);
        aiKnowledgeBaseService.updateById(entity);
        return Result.success(null);
    }

    @ApiOperation("删除知识库条目")
    @DeleteMapping("/{id}")
    public Result<Void> deleteKnowledge(@PathVariable Long id) {
        aiKnowledgeBaseService.deleteKnowledge(id);
        return Result.success(null);
    }

    @ApiOperation("搜索知识库")
    @GetMapping("/search")
    public Result<List<Map<String, Object>>> search(@RequestParam String keyword) {
        Long schoolId = getCurrentSchoolId();
        List<AiKnowledgeBaseEntity> list = aiKnowledgeBaseService.search(schoolId, keyword);
        return Result.success(list.stream().map(this::toMap).toList());
    }

    private Map<String, Object> toMap(AiKnowledgeBaseEntity entity) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", entity.getId());
        map.put("schoolId", entity.getSchoolId());
        map.put("category", entity.getCategory());
        map.put("question", entity.getQuestion());
        map.put("answer", entity.getAnswer());
        map.put("status", entity.getStatus());
        map.put("sort", entity.getSort());
        map.put("createTime", entity.getCreateTime());
        map.put("updateTime", entity.getUpdateTime());
        return map;
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

    private Long getCurrentUserId() {
        try {
            return StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            return 0L;
        }
    }
}