package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@ApiModel("平时分记录信息保存参数")
public class StudentUsuallyTaskSaveReqModel {
    @ApiModelProperty(value = "学校id", required = true)
    @NotNull(message = "学校id不能为空")
    private Long schoolId;
    @NotNull(message = "测验类型不能为空")
    @ApiModelProperty(value = "平时成绩类型id", required = true)
    private Long typeId;
    @ApiModelProperty(value = "测验名称")
    private String name;
    @NotNull(message = "次数不能为空")
    @ApiModelProperty(value = "次数", required = true)
    private Integer frequency;
    @NotNull(message = "测验时间不能为空")
    @ApiModelProperty(value = "测验时间", required = true)
    private LocalDate testDate;
    @NotNull(message = "学段id不能为空")
    @ApiModelProperty(value = "学段id", required = true)
    private Long periodId;
    @NotNull(message = "班级id不能为空")
    @ApiModelProperty(value = "班级id", required = true)
    private Long classId;
    @NotNull(message = "科目id不能为空")
    @ApiModelProperty(value = "科目id", required = true)
    private Long subjectId;
    @ApiModelProperty(value = "备注")
    private String remark;
}