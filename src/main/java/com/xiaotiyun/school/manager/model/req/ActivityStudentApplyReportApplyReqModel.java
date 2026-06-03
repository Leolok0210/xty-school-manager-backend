package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.model.res.ActivityStudentApplyReportVolunteerResModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 报名请求模型
 */
@Data
@ApiModel("报名请求模型")
public class ActivityStudentApplyReportApplyReqModel {
    @ApiModelProperty(value = "学校ID")
    private Long schoolId;

    @NotNull(message = "活动ID不能为空")
    @ApiModelProperty(value = "活动ID", required = true)
    private Long activityId;

    @NotNull(message = "学生ID不能为空")
    @ApiModelProperty(value = "学生ID", required = true)
    private Long studentId;

    @NotEmpty(message = "志愿列表不能为空")
    @Valid
    @ApiModelProperty(value = "志愿列表", required = true)
    private List<ActivityStudentApplyReportVolunteerResModel> volunteerList;
} 