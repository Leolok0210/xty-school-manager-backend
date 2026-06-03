package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@ApiModel("教室类型请求参数")
public class ClassroomTypeReqModel {

    @NotBlank(message = "类型名称不能为空")
    @Size(max = 20, message = "类型名称最长20个字符")
    @ApiModelProperty(value = "类型名称", required = true)
    private String name;
}