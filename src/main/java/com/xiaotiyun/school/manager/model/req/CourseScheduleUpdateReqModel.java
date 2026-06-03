package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("课表更新请求参数")
public class CourseScheduleUpdateReqModel {

    @NotNull(message = "科目ID不能为空")
    @ApiModelProperty(value = "科目ID", required = true)
    private Long subjectId;

    @ApiModelProperty("教室ID")
    private Long classroomId;
}