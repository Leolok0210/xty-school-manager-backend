package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lesson")
@ApiModel(value = "课节实体")
public class LessonEntity extends BaseEntity {
    @ApiModelProperty(value = "学校ID", required = true)
    private Long schoolId;

    @ApiModelProperty(value = "级组ID", required = true)
    private Long gradeId;

    @ApiModelProperty(value = "课节名称", required = true)
    private String name;

    @ApiModelProperty(value = "开始时间", required = true)
    private LocalTime startTime;

    @ApiModelProperty(value = "结束时间", required = true)
    private LocalTime endTime;
} 