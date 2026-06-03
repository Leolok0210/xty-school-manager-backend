package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@ApiModel("比赛保存参数")
public class CompetitionSaveReqModel {
    @ApiModelProperty(value = "学校ID", required = true)
    @NotNull(message = "学校ID不能为空")
    private Long schoolId;

    @ApiModelProperty(value = "学年", required = true)
    @NotBlank(message = "学年不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "学年格式应为YYYY-YYYY")
    private String schoolYear;

    @ApiModelProperty(value = "比赛名称", required = true)
    @NotBlank(message = "比赛名称不能为空")
    @Size(max = 50, message = "比赛名称最长50个字符")
    private String competitionName;

    @ApiModelProperty(value = "开始日期", required = true)
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @ApiModelProperty(value = "结束日期", required = true)
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    @ApiModelProperty(value = "主办单位", required = true)
    @NotBlank(message = "主办单位不能为空")
    @Size(max = 50, message = "主办单位最长50个字符")
    private String organizer;

    @ApiModelProperty(value = "比赛地点", required = true)
    @NotBlank(message = "比赛地点不能为空")
    @Size(max = 100, message = "比赛地点最长100个字符")
    private String location;
} 