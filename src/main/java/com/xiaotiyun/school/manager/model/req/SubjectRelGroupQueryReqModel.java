package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("根据学校id和级组id查询科目及关联请求")
public class SubjectRelGroupQueryReqModel {
    @ApiModelProperty("学校ID")
    @NotNull(message = "学校ID不能为空")
    private Long schoolId;

    @ApiModelProperty("级组ID")
    @NotNull(message = "级组ID不能为空")
    private Long groupId;

    @ApiModelProperty("学科名称")
    private String name;

    @ApiModelProperty("是否参与平均分")
    private Integer countedInAverage;
} 