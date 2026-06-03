package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalTime;
import java.util.List;

@Data
@ApiModel("教师考勤规则请求参数")
public class TeacherAttendanceRuleSaveReqModel {
    @NotNull(message = "学校id不能为空")
    @ApiModelProperty(value = "学校id", required = true)
    private Long schoolId;

    @NotBlank(message = "规则名称不能为空")
    @Size(max = 50, message = "规则名称最长50个字符")
    @ApiModelProperty(value = "规则名称", required = true)
    private String ruleName;

    @NotNull(message = "规则类型不能为空")
    @ApiModelProperty(value = "规则类型(0.默认规则;1.特殊规则)", required = true)
    private Integer type;

    @ApiModelProperty(value = "用户ID列表(选择到用户时必传)")
    private List<Long> userIds;

    @ApiModelProperty(value = "部门ID列表(选择到部门时必传)")
    private List<Long> depIds;

    @NotEmpty(message = "生效范围不能为空")
    @ApiModelProperty(value = "生效范围(1-7.周一到周日)", required = true)
    private List<Integer> effectiveScope;

    @NotNull(message = "上班时间不能为空")
    @ApiModelProperty(value = "上班时间", required = true)
    private LocalTime clockInTime;

    @NotNull(message = "下班时间不能为空")
    @ApiModelProperty(value = "下班时间", required = true)
    private LocalTime clockOutTime;
} 