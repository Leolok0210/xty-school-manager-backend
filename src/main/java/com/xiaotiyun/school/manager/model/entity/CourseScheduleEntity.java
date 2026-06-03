package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("course_schedule")
@ApiModel(value = "课程表实体")
public class CourseScheduleEntity extends BaseEntity {
    @ApiModelProperty(value = "学校ID", required = true)
    private Long schoolId;

    @ApiModelProperty(value = "学段ID", required = true)
    private Long periodId;

    @ApiModelProperty(value = "级组id", required = true)
    private Long gradeId;

    @ApiModelProperty(value = "班级ID", required = true)
    private Long classId;

    @ApiModelProperty(value = "科目ID", required = true)
    private Long subjectId;

    @ApiModelProperty(value = "科目名称", required = true)
    private String subjectName;

    @ApiModelProperty(value = "教师ID", required = true)
    private Long teacherId;

    @ApiModelProperty(value = "教师姓名", required = true)
    private String teacherName;

    @ApiModelProperty(value = "课节ID", required = true)
    private Long lessonId;

    @ApiModelProperty(value = "课节名称", required = true)
    private String lessonName;

    @ApiModelProperty("教室ID")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long classroomId;

    @ApiModelProperty("教室名称")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String classroomName;

    @ApiModelProperty(value = "课程日期", required = true)
    private LocalDate courseDate;

    @ApiModelProperty(value = "开始时间", required = true)
    private LocalTime startTime;

    @ApiModelProperty(value = "结束时间", required = true)
    private LocalTime endTime;
} 