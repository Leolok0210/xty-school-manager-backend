package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("查询学段请求")
public class SemesterQueryReqModel {
    @ApiModelProperty("学年(格式:2025-2026)")
    private String schoolYear;

    // 学生端使用
    @ApiModelProperty("级组ID")
    private Long groupId;
} 