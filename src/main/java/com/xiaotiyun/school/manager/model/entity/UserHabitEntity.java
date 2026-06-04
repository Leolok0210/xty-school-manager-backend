package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ai_user_habit")
public class UserHabitEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long schoolId;

    /**
     * JSON array of frequent keywords, e.g. ["中五1班", "物理成績"]
     */
    private String frequentKeywords;

    /**
     * JSON array of frequent class names, e.g. ["中五1班", "中三2班"]
     */
    private String frequentClasses;

    /**
     * Total query count
     */
    private Integer queryCount;

    /**
     * Last query timestamp
     */
    private Date lastQueryTime;

    /**
     * Preferred language: 繁體 / 簡體
     */
    private String preferredLanguage;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Integer deleted;
}