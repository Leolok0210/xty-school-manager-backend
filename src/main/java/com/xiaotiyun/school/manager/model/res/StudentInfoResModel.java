package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class StudentInfoResModel {
    @ApiModelProperty(value = "学生UserID")
    private String wxIds;

    @ApiModelProperty(value = "学生名字")
    private String name;
}
