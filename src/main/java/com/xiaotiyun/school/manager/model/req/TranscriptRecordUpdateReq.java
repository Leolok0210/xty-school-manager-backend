package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TranscriptRecordUpdateReq {
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "id", required = true)
    private Long id;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "处理状态：0:待处理；1：处理成功；2：处理失败", required = true)
    private Integer status;

    @ApiModelProperty(value = "班级成绩压缩包下载地址")
    private String zipUrl;
}
