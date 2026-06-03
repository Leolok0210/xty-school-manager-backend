package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("课表请求参数")
public class CourseScheduleSaveReqModel {
    @NotNull(message = "学年不能为空")
    @ApiModelProperty(value = "学年", required = true)
    private String schoolYear;

    @NotNull(message = "学段ID不能为空")
    @ApiModelProperty(value = "学段ID", required = true)
    private Long periodId;

    @NotNull(message = "班级ID不能为空")
    @ApiModelProperty(value = "班级ID", required = true)
    private Long classId;

    @NotNull(message = "科目ID不能为空")
    @ApiModelProperty(value = "科目ID", required = true)
    private Long subjectId;

    @ApiModelProperty("教室ID")
    private Long classroomId;

    @ApiModelProperty(value = "课表日期", required = true)
    @Valid
    @NotEmpty(message = "课表日期不能为空")
    private List<CourseScheduleDateSaveReqModel> substituteDateList;
}