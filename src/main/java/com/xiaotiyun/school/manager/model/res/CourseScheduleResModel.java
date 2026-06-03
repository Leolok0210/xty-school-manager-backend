package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@ApiModel("课表响应参数")
public class CourseScheduleResModel {
    @ApiModelProperty("课表ID")
    private Long id;

    @ApiModelProperty("学段ID")
    private Long periodId;

    @ApiModelProperty("班级ID")
    private Long classId;

    @ApiModelProperty("科目ID")
    private Long subjectId;

    @ApiModelProperty("科目名称")
    private String subjectName;

    @ApiModelProperty("教师ID")
    private Long teacherId;

    @ApiModelProperty("教师名称")
    private String teacherName;

    @ApiModelProperty("课节ID")
    private Long lessonId;

    @ApiModelProperty("课节名称")
    private String lessonName;

    @ApiModelProperty("教室ID")
    private Long classroomId;

    @ApiModelProperty("教室名称")
    private String classroomName;

    @ApiModelProperty("课程日期")
    private LocalDate courseDate;

    @ApiModelProperty("开始时间")
    private LocalTime startTime;

    @ApiModelProperty("结束时间")
    private LocalTime endTime;
} 