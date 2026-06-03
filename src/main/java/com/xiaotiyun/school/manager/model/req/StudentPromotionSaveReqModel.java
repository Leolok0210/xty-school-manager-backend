package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("升留级登记保存请求参数")
public class StudentPromotionSaveReqModel {
    @NotNull(message = "学年不能为空")
    @ApiModelProperty(value = "学年", required = true, example = "2023-2024")
    private String schoolYear;

    @NotNull(message = "班级ID不能为空")
    @ApiModelProperty(value = "班级ID", required = true)
    private Long classId;

    @ApiModelProperty(value = "登记学生信息", required = true)
    @Valid
    @NotEmpty(message = "登记学生信息不能为空")
    private List<StudentPromotionSaveStudentReqModel> studentInfos;
}