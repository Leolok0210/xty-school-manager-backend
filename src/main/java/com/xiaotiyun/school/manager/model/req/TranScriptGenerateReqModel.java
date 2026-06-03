package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "生成成绩单请求参数")
public class TranScriptGenerateReqModel {

    @NotNull(message = "班级ID不能为空 -1 全部")
    @ApiModelProperty(value = "班级ID", required = true)
    private Long classId;
} 