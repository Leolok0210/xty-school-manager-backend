package com.xiaotiyun.school.manager.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;



@Data
public class CompetitionStudentCountDTO {


    private Long studentId;

    @ApiModelProperty("大功次数")
    private Integer meritBig;

    @ApiModelProperty("小功次数")
    private Integer meritSmall;

    @ApiModelProperty("优点次数")
    private Integer meritAdvantage;

    @ApiModelProperty("大过次数")
    private Integer demeritBig;

    @ApiModelProperty("小过次数")
    private Integer demeritSmall;

    @ApiModelProperty("缺点次数")
    private Integer demeritShortcoming;
}
