package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@ApiModel("事项保存参数")
public class SchoolCalendarEventSaveReqModel {
    @ApiModelProperty(value = "校历ID", required = true)
    @NotNull(message = "校历ID不能为空")
    private Long schoolICalendarId;

    @ApiModelProperty(value = "事项类型", required = true)
    @NotNull(message = "事项类型不能为空")
    private Integer eventType;

    @ApiModelProperty(value = "开始时间", required = true)
    @NotNull(message = "开始时间不能为空")
    private LocalDate startDate;

    @ApiModelProperty(value = "结束时间", required = true)
    @NotNull(message = "结束时间不能为空")
    private LocalDate endDate;

    @ApiModelProperty(value = "事项描述", required = true)
    @NotBlank(message = "事项描述不能为空")
    @Size(max = 100, message = "事项描述最长100个字符")
    private String eventDescription;
} 