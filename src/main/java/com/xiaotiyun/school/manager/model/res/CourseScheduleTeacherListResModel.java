package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel("课表响应参数")
public class CourseScheduleTeacherListResModel {
    @ApiModelProperty("课程日期")
    private LocalDate courseDate;

    @ApiModelProperty("课程信息")
    private List<CourseScheduleTeacherListDetailsResModel> courseDetails;
}