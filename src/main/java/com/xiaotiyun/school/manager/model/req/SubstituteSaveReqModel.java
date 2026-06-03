package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class SubstituteSaveReqModel {

    @NotNull(message = "学段id不能为空")
    @ApiModelProperty(value = "学段id", required = true)
    private Long periodId;

    @NotNull(message = "班级id不能为空")
    @ApiModelProperty(value = "班级id", required = true)
    private Long classId;

    @NotNull(message = "科目id不能为空")
    @ApiModelProperty(value = "科目id", required = true)
    private Long subjectId;

    @NotNull(message = "原任课老师ID不能为空")
    @ApiModelProperty(value = "原任课老师id", required = true)
    private Long originalTeacherId;

    @NotNull(message = "代课老师id不能为空")
    @ApiModelProperty(value = "代课老师id", required = true)
    private Long substituteTeacherId;

    @ApiModelProperty(value = "代课日期", required = true)
    @Valid
    @NotEmpty(message = "代课日期不能为空")
    private List<SubstituteDateSaveReqModel> substituteDateList;

    @Size(max = 100, message = "备注不能超过100个字符")
    @ApiModelProperty(value = "备注")
    private String remark;
}