package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel("比赛列表响应信息")
public class CompetitionPageResModel {
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
} 