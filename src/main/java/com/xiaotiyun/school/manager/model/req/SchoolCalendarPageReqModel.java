package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("校历分页查询参数")
public class SchoolCalendarPageReqModel extends PageReqModel {
    @ApiModelProperty(value = "学校ID", required = true)
    @NotNull(message = "学校id不能为空")
    private Long schoolId;

    @ApiModelProperty(value = "校历名称")
    private String calendarName;
}