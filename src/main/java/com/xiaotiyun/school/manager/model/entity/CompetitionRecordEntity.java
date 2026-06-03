package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("competition_record")
public class CompetitionRecordEntity extends BaseEntity {
    @ApiModelProperty(value = "比赛ID", required = true, example = "1")
    private Long competitionId;

    @ApiModelProperty(value = "学生ID", required = true, example = "10001")
    private Long studentId;

    @ApiModelProperty(value = "班级ID", required = true, example = "201")
    private Long classId;

    @ApiModelProperty("获得的奖励（最多50字）")
    @Size(max = 50)
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