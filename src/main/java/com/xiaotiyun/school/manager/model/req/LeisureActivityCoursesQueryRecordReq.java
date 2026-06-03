package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 余暇活动课程记录查询参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("余暇活动课程记录查询参数")
public class LeisureActivityCoursesQueryRecordReq extends PageReqModel {

    /**
     * 学校ID
     */
    @ApiModelProperty(value = "学校ID")
    private Long schoolId;

    /**
     * 课程名称，模糊搜索
     */
    @ApiModelProperty(value = "课程名称，模糊搜索")
    private String name;

    /**
     * 活动ID
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty("活动ID")
    private Long activityId;

    /**
     * 录取状态
     */
    @ApiModelProperty("录取状态 0:未录满 1:已录满")
    private Integer status;
}
