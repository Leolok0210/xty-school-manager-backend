package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "学生表现统计返回参数")
public class StudentPerformanceTotalResModel {

    @ApiModelProperty("类型 1-上课违规 2-欠作业 3-仪表不符 5-欠课本,4-迟到,6-缺席,7-请假,8-优点,9-大功,10-小功,11-缺点,12-大过,13-小过")
    private int type;

    @ApiModelProperty("次数")
    private int num;
}
