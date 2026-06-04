package com.xiaotiyun.school.manager.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.dao.UserHabitDao;
import com.xiaotiyun.school.manager.model.entity.UserHabitEntity;
import com.xiaotiyun.school.manager.service.UserHabitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
public class UserHabitServiceImpl extends ServiceImpl<UserHabitDao, UserHabitEntity> implements UserHabitService {

    @Override
    public UserHabitEntity getByUserId(Long userId) {
        return this.lambdaQuery()
                .eq(UserHabitEntity::getUserId, userId)
                .one();
    }

    @Override
    public void updateHabit(Long userId, Long schoolId, String queryContent) {
        UserHabitEntity habit = getByUserId(userId);

        if (habit == null) {
            habit = new UserHabitEntity();
            habit.setUserId(userId);
            habit.setSchoolId(schoolId);
            habit.setQueryCount(1);
            habit.setPreferredLanguage("繁體");
            habit.setFrequentClasses("[]");
            habit.setFrequentKeywords("[]");
        } else {
            habit.setQueryCount(habit.getQueryCount() + 1);
        }

        habit.setLastQueryTime(new Date());

        // Extract class names from query
        extractAndUpdateClasses(habit, queryContent);

        // Extract keywords from query
        extractAndUpdateKeywords(habit, queryContent);

        this.saveOrUpdate(habit);
    }

    private void extractAndUpdateClasses(UserHabitEntity habit, String queryContent) {
        // Pattern to match class names like 中五1班, 中三2班, etc.
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[\\u4e00-\\u9fa5]+[一二三四五六七八九十]+[0-9]*班");
        java.util.regex.Matcher matcher = pattern.matcher(queryContent);

        Set<String> classes = new HashSet<>();
        if (habit.getFrequentClasses() != null && !habit.getFrequentClasses().isEmpty()) {
            try {
                classes.addAll(JSON.parseArray(habit.getFrequentClasses(), String.class));
            } catch (Exception e) {
                log.debug("Failed to parse frequent classes", e);
            }
        }

        while (matcher.find()) {
            classes.add(matcher.group());
        }

        // Keep only top 10 most recent
        List<String> classList = new ArrayList<>(classes);
        if (classList.size() > 10) {
            classList = classList.subList(classList.size() - 10, classList.size());
        }

        habit.setFrequentClasses(JSON.toJSONString(classList));
    }

    private void extractAndUpdateKeywords(UserHabitEntity habit, String queryContent) {
        // Common education keywords
        String[] keywordPatterns = {"成績", "分數", "物理", "數學", "英文", "中文", "化學", "生物",
                                     "歷史", "地理", "學生", "班級", "考勤", "獎懲", "學期", "日常"};

        Set<String> keywords = new HashSet<>();
        if (habit.getFrequentKeywords() != null && !habit.getFrequentKeywords().isEmpty()) {
            try {
                keywords.addAll(JSON.parseArray(habit.getFrequentKeywords(), String.class));
            } catch (Exception e) {
                log.debug("Failed to parse frequent keywords", e);
            }
        }

        for (String pattern : keywordPatterns) {
            if (queryContent.contains(pattern)) {
                keywords.add(pattern);
            }
        }

        // Keep only top 20 keywords
        List<String> keywordList = new ArrayList<>(keywords);
        if (keywordList.size() > 20) {
            keywordList = keywordList.subList(keywordList.size() - 20, keywordList.size());
        }

        habit.setFrequentKeywords(JSON.toJSONString(keywordList));
    }

    @Override
    public List<String> getFrequentClasses(Long userId) {
        UserHabitEntity habit = getByUserId(userId);
        if (habit == null || habit.getFrequentClasses() == null) {
            return new ArrayList<>();
        }
        try {
            return JSON.parseArray(habit.getFrequentClasses(), String.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<String> getFrequentKeywords(Long userId) {
        UserHabitEntity habit = getByUserId(userId);
        if (habit == null || habit.getFrequentKeywords() == null) {
            return new ArrayList<>();
        }
        try {
            return JSON.parseArray(habit.getFrequentKeywords(), String.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}