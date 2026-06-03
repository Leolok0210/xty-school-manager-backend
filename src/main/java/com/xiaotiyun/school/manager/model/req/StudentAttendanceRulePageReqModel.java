package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("学生出勤规则分页查询参数")
public class StudentAttendanceRulePageReqModel extends PageReqModel {
    @NotNull(message = "学校ID不能为空")
    @ApiModelProperty(value = "学校ID", example = "1001")
    private Long schoolId;
}