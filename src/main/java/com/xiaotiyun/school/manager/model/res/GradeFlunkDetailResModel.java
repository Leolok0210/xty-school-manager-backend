package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "不合格成绩返回详情对象")
public class GradeFlunkDetailResModel {
    @ApiModelProperty(value = "级组ID", example = "1")
    private Long groupId;

    @ApiModelProperty(value = "级组名称", example = "高一")
    private String groupName;

    @ApiModelProperty(value = "班级ID", example = "1")
    private Long classId;

    @ApiModelProperty(value = "班级名称", example = "高一(1)班")
    private String className;

    @ApiModelProperty(value = "不合格数据")
    private List<GradeFlunkSemesterResModel> details;
}