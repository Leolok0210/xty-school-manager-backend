package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("school_calendar_date_type")
public class SchoolCalendarDateTypeEntity extends BaseEntity {
    /**
     * 校历ID
     */
    private Long schoolCalendarId;
    /**
     * 类型(1:工作日,2:双休日,3:假期)
     */
    private Integer type;
    /**
     * 适用类型(1:老师,2:学生)
     */
    private Integer applyType;
    /**
     * 日期
     */
    private LocalDate calendarDate;
}