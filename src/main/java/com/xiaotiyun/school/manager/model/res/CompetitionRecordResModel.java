package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

@Data
@ApiModel("参赛记录响应信息")
public class CompetitionRecordResModel {
    @ApiModelProperty("记录ID")
    private Long id;

    @ApiModelProperty("比赛id")
    private Long competitionId;

    @ApiModelProperty("学生姓名")
    private String studentName;

    @ApiModelProperty("学生编号")
    private String studentNo;

    @ApiModelProperty("级组名称")
    private String gradeName;

    @ApiModelProperty("班级名称")
    private String className;

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