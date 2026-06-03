package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("任教设置新增请求信息")
public class TeachingSettingAddReqModel {

    @ApiModelProperty("ID")
    private Long id;

    @NotNull(message = "学年不能为空")
    @ApiModelProperty(value = "学年",required = true)
    private String sid;

    @NotNull(message = "学期不能为空")
    @ApiModelProperty(value = "班级ID",required = true)
    private Long classId;

    @NotNull(message = "科目不能为空")
    @ApiModelProperty(value = "科目ID",required = true)
    private Long subjectId;

    @ApiModelProperty(value = "任教老师ID",required = false)
    private Long teacherId;
}