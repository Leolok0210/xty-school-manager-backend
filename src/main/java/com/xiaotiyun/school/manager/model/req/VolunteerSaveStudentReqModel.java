package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("义工服务学生信息保存参数")
public class VolunteerSaveStudentReqModel {

    @ApiModelProperty(value = "班级ID", required = true)
    @NotNull(message = "班级不能为空")
    private Long classId;

    @ApiModelProperty(value = "学生ID", required = true)
    @NotNull(message = "学生不能为空")
    private Long studentId;

    @ApiModelProperty("班级名称")
    @NotNull(message = "班级名称不能为空")
    private String className;

    @ApiModelProperty("级组")
    @NotNull(message = "级组不能为空")
    private String gradeName;
}
