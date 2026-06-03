package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("holidays")
public class HolidaysEntity extends BaseEntity {
    /**
     * 年份
     */
    private Integer year;
    /**
     * 假日名称
     */
    private String name;
    /**
     * 假日日期
     */
    private LocalDate holidaysDate;
}