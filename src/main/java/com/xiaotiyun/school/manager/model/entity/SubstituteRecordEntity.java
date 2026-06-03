package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("substitute_record")
@ApiModel(value = "代课记录实体")
public class SubstituteRecordEntity extends BaseEntity {
    @ApiModelProperty(value = "学校ID", required = true)
    private Long schoolId;

    @ApiModelProperty(value = "班级ID", required = true)
    private Long classId;

    @ApiModelProperty(value = "科目ID", required = true)
    private Long subjectId;

    @ApiModelProperty(value = "学段ID", required = true)
    private Long periodId;

    @ApiModelProperty(value = "原任课老师ID", required = true)
    private Long originalTeacherId;

    @ApiModelProperty(value = "代课老师ID", required = true)
    private Long substituteTeacherId;

    @ApiModelProperty(value = "代课日期", required = true)
    private LocalDate substituteDate;

    @ApiModelProperty(value = "课表id", required = true)
    private Long courseScheduleId;

    @ApiModelProperty(value = "课节ID", required = true)
    private Long lessonId;

    @ApiModelProperty(value = "课节名称", required = true)
    private String lessonName;

    @ApiModelProperty("备注")
    private String remark;
} 