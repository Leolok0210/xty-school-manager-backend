package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("各科优良成绩返回参数")
public class ExcellentAndGoodGradesResModel {
    

    @ApiModelProperty(value = "汇总")
    private GradesStatisticsExcelResModel all;

    @ApiModelProperty(value = "根据班级分组")
    private GradesStatisticsExcelResModel groupByClass;

}