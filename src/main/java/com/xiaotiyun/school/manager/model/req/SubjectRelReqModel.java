package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("科目关联批量新增请求")
public class SubjectRelReqModel {

    private Long id;

    @ApiModelProperty("级组id")
    @NotNull(message = "级组id不能为空")
    private Long groupId;

    @ApiModelProperty("科目id 原始科目id，不是年级科目表（科目关联表）id")
    @NotNull(message = "科目id不能为空")
    private Long subjectId;

    @ApiModelProperty("序号")
    private Integer number;

    @ApiModelProperty("是否计入平均分 (0. 否，1. 是)")
    private Integer countedInAverage;

    @ApiModelProperty("文科理科：0-公共，1-文科，2-理科 3-商科")
    private Integer artsScience;

    @ApiModelProperty("1-选修 2-必修")
    private Integer subjectType;

    @ApiModelProperty("学校ID")
    @NotNull(message = "学校ID不能为空")
    private Long schoolId;
} 