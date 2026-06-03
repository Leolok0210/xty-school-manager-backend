package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class SchoolCalendarEventDetailsSaveReqModel {
    @ApiModelProperty(value = "事项类型(1:教师假期,2:考试,3:活动,4:其他,5:学生假期)")
    private Integer eventType;

    @ApiModelProperty(value = "事项描述")
    @Size(max = 100, message = "事项描述最长100个字符")
    private String eventDescription;
} 