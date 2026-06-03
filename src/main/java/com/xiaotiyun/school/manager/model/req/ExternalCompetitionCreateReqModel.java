package com.xiaotiyun.school.manager.model.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("校外比赛创建请求")
public class ExternalCompetitionCreateReqModel {
    @ApiModelProperty(value = "ID,修改时必传", required = true)
    private Long id;

    @NotNull(message = "学校id不能为空")
    @ApiModelProperty(value = "学校id", required = true)
    private Long schoolId;

    @NotBlank(message = "学年不能为空")
    @Pattern(regexp = "\\d{4}-\\d{4}", message = "学年格式应为YYYY-YYYY")
    @ApiModelProperty(value = "学年", required = true, example = "2023-2024")
    private String schoolYear;

    @NotBlank(message = "比赛项目不能为空")
    @Size(max = 50, message = "比赛项目最多50个字符")
    @ApiModelProperty(value = "比赛项目", required = true)
    private String name;

    @NotBlank(message = "主办单位不能为空")
    @Size(max = 50, message = "主办单位最多50个字符")
    @ApiModelProperty(value = "主办单位", required = true)
    private String organizer;

    @NotBlank(message = "指导老师不能为空")
    @Size(max = 50, message = "指导老师最多50个字符")
    @ApiModelProperty(value = "指导老师", required = true)
    private String advisor;

    @NotNull(message = "开始时间不能为空")
    @ApiModelProperty(value = "开始时间", required = true, example = "2023-10-10 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    @NotNull(message = "颁奖时间不能为空")
    @ApiModelProperty(value = "颁奖时间", required = true, example = "2023-10-10 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime prizeTime;

    @NotNull(message = "范畴ID不能为空")
    @ApiModelProperty(value = "范畴ID", required = true)
    private Long categoryId;

    @NotBlank(message = "范畴名称不能为空")
    @ApiModelProperty(value = "范畴名称", required = true)
    private String categoryName;

    @NotBlank(message = "是否具有代表性不能为空")
    @ApiModelProperty(value = "是否具有代表性", required = true)
    private String representative;

    @NotNull(message = "组别数量不能为空")
    @ApiModelProperty(value = "组别数量", required = true)
    private Integer groupSum;

    @ApiModelProperty(value = "地区,1-校内、2-港澳区、3-埠際或國際", required = true)
    private Integer area;

    @Size(max = 50, message = "主办单位最多50个字符")
    @ApiModelProperty(value = "活动地区", required = true)
    private String activityArea;

    @Size(max = 100, message = "备注一最多100个字符")
    @ApiModelProperty("备注一")
    private String remark1;

    @NotEmpty(message = "组别列表不能为空")
    @ApiModelProperty(value = "组别列表", required = true)
    private List<ExternalCompetitionGroupCreateReqModel> groups;
} 