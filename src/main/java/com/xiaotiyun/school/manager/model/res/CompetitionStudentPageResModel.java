package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel("比赛列表响应信息")
public class CompetitionStudentPageResModel {
    @ApiModelProperty("比赛ID")
    private Long id;
    
    @ApiModelProperty("学校ID")
    private Long schoolId;
    
    @ApiModelProperty("比赛名称")
    private String competitionName;
    
    @ApiModelProperty("开始日期")
    private LocalDate startDate;
    
    @ApiModelProperty("结束日期")
    private LocalDate endDate;
    
    @ApiModelProperty("主办单位")
    private String organizer;
    
    @ApiModelProperty("比赛地点")
    private String location;

    @ApiModelProperty("学年")
    private String schoolYear;

    @ApiModelProperty("比赛奖励")
    private String award;

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