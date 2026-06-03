package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "各班平均分返回对象")
public class GradeClassAvgResModel {
    @ApiModelProperty(value = "级组ID", example = "1")
    private Long classGroupId;

    @ApiModelProperty(value = "级组名称", example = "高一")
    private String classGroupName;

    @ApiModelProperty(value = "班级ID", example = "1")
    private Long classId;

    @ApiModelProperty(value = "班级名称", example = "高一(1)班")
    private String className;

    @ApiModelProperty(value = "平均分", example = "89.11")
    private String averageScore;

    @ApiModelProperty(value = "班级人数", example = "20")
    private Integer classSize;

}