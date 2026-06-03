package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StudentImageDownloadReqModel {
    @NotNull(message = "班级id不能为空")
    @ApiModelProperty(value = "班级id", required = true)
    private Long classId;
    @ApiModelProperty(value = "命名方式(1.班内号_学生编号;2.班级号_教青局编号;3.班级名称_班内号_中文姓名;4.系统内相片名称)")
    private Integer type;
}
