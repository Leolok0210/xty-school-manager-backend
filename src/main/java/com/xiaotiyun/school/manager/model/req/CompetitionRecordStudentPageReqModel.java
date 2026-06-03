package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("参赛记录分页查询参数")
public class CompetitionRecordStudentPageReqModel extends PageReqModel {
    @NotNull(message = "学生id不能为空")
    @ApiModelProperty("学生id")
    private Long studentId;
}