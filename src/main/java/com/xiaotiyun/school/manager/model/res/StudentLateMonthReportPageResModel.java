package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "学生迟到统计-月-分页查询返回对象")
public class StudentLateMonthReportPageResModel {
    @ApiModelProperty(value = "总记录数")
    private int total;

    @ApiModelProperty(value = "数据列表")
    private List<StudentLateMonthReportResModel> list;

    @ApiModelProperty(value = "当前页码")
    private int pageNum;

    @ApiModelProperty(value = "每页大小")
    private int pageSize;

    @ApiModelProperty(value = "当前页记录数")
    private int size;

    @ApiModelProperty(value = "总页数")
    private int pages;

}
