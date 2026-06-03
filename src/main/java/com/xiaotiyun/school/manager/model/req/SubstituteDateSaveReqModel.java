package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel("代课日期请求参数")
public class SubstituteDateSaveReqModel {

    @NotNull(message = "代课日期不能为空")
    @ApiModelProperty(value = "代课日期", required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate substituteDate;

    @NotNull(message = "课节id不能为空")
    @ApiModelProperty(value = "课节id", required = true)
    private List<Long> lessonIds;
}