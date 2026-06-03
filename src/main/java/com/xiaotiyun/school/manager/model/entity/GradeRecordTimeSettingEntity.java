package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("grade_record_time_setting")
public class GradeRecordTimeSettingEntity extends BaseEntity {
    
    /**
     * 学校ID
     */
    private Long schoolId;
    
    /**
     * 学年
     */
    private String schoolYear;
    
    /**
     * 学段ID
     */
    private Long semesterId;
    
    /**
     * 学部(1:幼稚园 2:小学 3:中学)
     */
    private Integer department;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
} 