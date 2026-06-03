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
@ApiModel(description = "余暇活动记录分页请求对象")
public class LeisureActivityRecordPageReqModel extends PageReqModel {
    @ApiModelProperty(value = "学校ID")
    private Long schoolId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学年")
    private String schoolYear;

    @ApiModelProperty(value = "学部 (1: 幼稚园, 2: 小学, 3: 中学)")
    private Integer department;

    @ApiModelProperty(value = "学段 ID")
    private Long semesterId;

    @ApiModelProperty(value = "活动名称,模糊搜索")
    private String name;
}