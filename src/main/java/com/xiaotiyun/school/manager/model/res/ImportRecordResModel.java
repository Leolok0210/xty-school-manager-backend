package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ImportRecordResModel {
    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty("错误行号")
    private String incorrectLineno;
    @ApiModelProperty("错误原因")
    private String incorrectReason;
}