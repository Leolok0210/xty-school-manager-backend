package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("school_calendar_event")
public class SchoolCalendarEventEntity extends BaseEntity {
    /**
     * 校历ID
     */
    @ApiModelProperty("校历ID")
    private Long schoolCalendarId;
    /**
     * 事项类型(1:教师假期,2:考试,3:活动,4:其他,5:学生假期)
     */
    @ApiModelProperty("事项类型")
    private Integer eventType;
    /**
     * 事项日期
     */
    @TableField("event_date")
    @NotNull
    private LocalDate eventDate;
    /**
     * 事项描述
     */
    @ApiModelProperty("事项描述")
    private String eventDescription;
    /**
     * 是否系统生成
     */
    private Boolean isSystem;
}