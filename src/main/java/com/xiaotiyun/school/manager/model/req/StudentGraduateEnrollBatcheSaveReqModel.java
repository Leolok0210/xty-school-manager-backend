package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class StudentGraduateEnrollBatcheSaveReqModel {
    @ApiModelProperty(value = "学校id", required = true)
    @NotNull(message = "学校id不能为空")
    private Long schoolId;
    @NotNull(message = "班级id不能为空")
    @ApiModelProperty(value = "班级id", required = true)
    private Long classId;
    @NotEmpty(message = "登记信息不能为空")
    @ApiModelProperty(value = "登记信息", required = true)
    @Valid
    private List<StudentGraduateEnrollBatcheSaveDetailsReqModel> details;
}