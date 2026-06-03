package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalTime;

@Data
public class CourseScheduleClassListDetailsResModel {
    @ApiModelProperty("课表ID")
    private Long id;

    @ApiModelProperty("科目ID")
    private Long subjectId;

    @ApiModelProperty("科目名称")
    private String subjectName;

    @ApiModelProperty("教师ID")
    private Long teacherId;

    @ApiModelProperty("教师名称")
    private String teacherName;

    @ApiModelProperty("是否代课")
    private Boolean isSubstitute;

    @ApiModelProperty("课节ID")
    private Long lessonId;

    @ApiModelProperty("课节名称")
    private String lessonName;

    @ApiModelProperty("教室类型ID")
    private Long classroomTypeId;

    @ApiModelProperty("教室类型名称ID")
    private String classroomTypeName;

    @ApiModelProperty("教室类型是否系统预设")
    private Boolean classroomTypeIsSystem;

    @ApiModelProperty("教室ID")
    private Long classroomId;

    @ApiModelProperty("教室名称")
    private String classroomName;

    @ApiModelProperty("开始时间")
    private LocalTime startTime;

    @ApiModelProperty("结束时间")
    private LocalTime endTime;
} 