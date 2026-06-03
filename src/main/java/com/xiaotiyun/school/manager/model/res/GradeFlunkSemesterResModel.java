package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "不合格成绩返回学段详情对象")
public class GradeFlunkSemesterResModel {
    @ApiModelProperty(value = "学段ID", example = "1")
    private Long semesterId;

    @ApiModelProperty(value = "学段名称", example = "高一")
    private String semesterName;

    @ApiModelProperty(value = "不合格人数", example = "1")
    private Integer flunkCount;

}