package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("比赛分页查询参数")
public class CompetitionPageReqModel extends PageReqModel {
    @ApiModelProperty(value = "学校ID", required = true)
    @NotNull(message = "学校ID不能为空")
    private Long schoolId;

    @ApiModelProperty("班级id")
    private Long classId;

    @ApiModelProperty("学生id")
    private Long studentId;

    @ApiModelProperty("比赛名称")
    private String competitionName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("开始日期范围起")
    private LocalDate startDateBegin;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("开始日期范围止")
    private LocalDate startDateEnd;

    @ApiModelProperty("学年查询条件")
    private String schoolYear;
} 