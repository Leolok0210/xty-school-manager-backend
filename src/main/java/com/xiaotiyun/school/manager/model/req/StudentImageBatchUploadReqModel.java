package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StudentImageBatchUploadReqModel {
    @NotNull(message = "学校id不能为空")
    @ApiModelProperty(value = "学校id", required = true)
    private Long schoolId;
    @NotNull(message = "压缩包文件url不能为空")
    @ApiModelProperty(value = "压缩包文件url")
    private String fileUrl;
    @NotNull(message = "文件名称不能为空")
    @ApiModelProperty(value = "文件名称")
    private String fileName;
}
