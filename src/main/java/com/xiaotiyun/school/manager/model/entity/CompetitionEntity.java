package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.Size;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("competition_info")
public class CompetitionEntity extends BaseEntity {
    @ApiModelProperty(value = "学校ID", required = true, example = "1001")
    private Long schoolId;
    
    @ApiModelProperty(value = "比赛名称", required = true, example = "校园编程大赛")
    private String competitionName;
    
    @ApiModelProperty(value = "开始日期", required = true, example = "2023-09-01")
    private LocalDate startDate;
    
    @ApiModelProperty(value = "结束日期", required = true)
    private LocalDate endDate;
    
    @ApiModelProperty(value = "主办单位", required = true)
    private String organizer;
    
    @ApiModelProperty(value = "比赛地点", required = true)
    private String location;
    
    @ApiModelProperty(value = "学年", required = true, example = "2023-2024")
    @Size(max = 64, message = "学年最长64个字符")
    private String schoolYear;
} 