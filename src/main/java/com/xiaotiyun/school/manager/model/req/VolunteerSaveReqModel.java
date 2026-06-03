package com.xiaotiyun.school.manager.model.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@ApiModel("义工服务保存参数")
public class VolunteerSaveReqModel {
    @ApiModelProperty(value = "学校ID", required = true)
    @NotNull(message = "学校ID不能为空")
    private Long schoolId;

    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "学年格式不正确")
    @ApiModelProperty(value = "学年", required = true)
    @NotBlank(message = "学年不能为空")
    private String schoolYear;

    @ApiModelProperty(value = "学生信息数组", required = true)
    @NotNull(message = "学生不能为空")
    private List<VolunteerSaveStudentReqModel> students;

    @ApiModelProperty(value = "活动名称", required = true)
    @Size(max = 50, message = "活动名称最长50个字符")
    @NotBlank(message = "活动名称不能为空")
    private String activityName;

    @ApiModelProperty(value = "机构名称", required = true)
    @Size(max = 50, message = "机构名称最长50个字符")
    @NotBlank(message = "机构名称不能为空")
    private String organization;

    @ApiModelProperty(value = "服务日期", required = true)
    @NotNull(message = "服务日期不能为空")
    private LocalDate serviceDate;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private LocalTime startTime;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private LocalTime endTime;

    @ApiModelProperty(value = "服务时数(单位：秒)",required = true)
    @NotNull(message = "服务时数不能为空")
    private Long serviceSeconds;

    @ApiModelProperty("服务性质")
    private String serviceNature;

    @ApiModelProperty("服务类别")
    private String serviceType;
}