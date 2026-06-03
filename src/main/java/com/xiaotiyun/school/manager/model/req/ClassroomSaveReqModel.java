package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@ApiModel("教室请求参数")
public class ClassroomSaveReqModel {

    @NotBlank(message = "教室名称不能为空")
    @Size(max = 20, message = "教室名称最长20个字符")
    @ApiModelProperty(value = "教室名称", required = true)
    private String name;

    @NotNull(message = "教室类型ID不能为空")
    @ApiModelProperty(value = "教室类型ID", required = true)
    private Long typeId;

    @Size(max = 20, message = "所在楼名最长20个字符")
    @ApiModelProperty("所在楼名")
    private String building;

    @Size(max = 20, message = "所在楼层最长20个字符")
    @ApiModelProperty("所在楼层")
    private String floor;
} 