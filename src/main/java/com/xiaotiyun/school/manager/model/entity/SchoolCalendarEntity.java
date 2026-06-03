package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("school_calendar")
public class SchoolCalendarEntity extends BaseEntity {
    /**
     * 学校ID
     */
    @TableField(value = "school_id")
    @NotNull private Long schoolId;
    /**
     * 校历名称
     */
    @TableField(value = "calendar_name")
    @NotBlank @Size(max = 50) 
    private String calendarName;
    /**
     * 开始日期
     */
    private LocalDate startDate;
    /**
     * 结束日期
     */
    private LocalDate endDate;
    /**
     * 创建人ID
     */
    private Long creatorId;
}