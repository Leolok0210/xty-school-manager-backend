package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SubjectSimpleResModel {
    @ApiModelProperty("科目ID")
    private Long id;
    @ApiModelProperty("科目名称")
    private String name;
}