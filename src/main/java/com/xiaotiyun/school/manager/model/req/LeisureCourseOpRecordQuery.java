package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;


@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "余暇活动课程操作记录查询请求参数")
public class LeisureCourseOpRecordQuery extends PageReqModel {

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "页面来源，1-预先导入，2-已匹配，3-无课程，4-二次报名")
    private Integer sourceId;

    @ApiModelProperty(value = "活动ID")
    private Long activityId;

    @ApiModelProperty(value = "课程ID")
    private Long courseId;
}
