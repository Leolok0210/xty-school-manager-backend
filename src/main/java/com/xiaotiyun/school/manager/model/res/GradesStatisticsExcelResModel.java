package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("成绩统计excel格式返回实体类")
public class GradesStatisticsExcelResModel {
    @ApiModelProperty(value = "标题头")
    private List<List<String>> title;
    @ApiModelProperty(value = "内容")
    private List<List<Object>> content;
}