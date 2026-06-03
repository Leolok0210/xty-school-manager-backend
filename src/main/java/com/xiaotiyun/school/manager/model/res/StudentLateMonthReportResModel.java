package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "学生迟到统计-月-分页查询返回对象")
public class StudentLateMonthReportResModel {
    @ApiModelProperty(value = "班级ID", example = "1")
    private Long classId;

    @ApiModelProperty(value = "班级名称", example = "高一(1)班")
    private String className;

    @ApiModelProperty(value = "级组名称", example = "高一")
    private String gradeGroupName;

    @ApiModelProperty(value = "迟到次数", example = "1")
    private Integer totalLateCount;

    @ApiModelProperty(value = "详情,按顺序每日的迟到次数", example = "[0,0,0,0,1,0,0,0]")
    private Integer[] detail;
}
