package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class SchoolCalendarEventSaveV230ReqModel {
    @ApiModelProperty(value = "校历ID", required = true)
    @NotNull(message = "校历ID不能为空")
    private Long schoolCalendarId;

    @ApiModelProperty(value = "开始时间", required = true)
    @NotNull(message = "开始时间不能为空")
    private LocalDate startDate;

    @ApiModelProperty(value = "结束时间", required = true)
    @NotNull(message = "结束时间不能为空")
    private LocalDate endDate;

    @ApiModelProperty(value = "日期类型", required = true)
    @NotEmpty(message = "日期类型不能为空")
    @Valid
    private List<SchoolCalendarDateTypeSaveV230ReqModel> dateTypes;

    @ApiModelProperty(value = "事项信息")
    private List<SchoolCalendarEventDetailsSaveReqModel> eventDetails;
}