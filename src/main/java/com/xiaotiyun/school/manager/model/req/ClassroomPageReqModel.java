package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ClassroomPageReqModel extends PageReqModel {
    @ApiModelProperty(value = "教室名称")
    private String name;
    @ApiModelProperty(value = "教室类型id")
    private Long typeId;
}