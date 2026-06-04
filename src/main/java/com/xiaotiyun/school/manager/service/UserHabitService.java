package com.xiaotiyun.school.manager.service;

import com.xiaotiyun.school.manager.model.entity.UserHabitEntity;

import java.util.List;

public interface UserHabitService {

    /**
     * Get user habit by userId
     */
    UserHabitEntity getByUserId(Long userId);

    /**
     * Update or create user habit record
     */
    void updateHabit(Long userId, Long schoolId, String queryContent);

    /**
     * Get frequent class names for a user
     */
    List<String> getFrequentClasses(Long userId);

    /**
     * Get frequent keywords for a user
     */
    List<String> getFrequentKeywords(Long userId);
}