package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "不合格成绩返回对象")
public class GradeFlunkResModel {
    @ApiModelProperty(value = "科目ID", example = "1")
    private Long subjectId;

    @ApiModelProperty(value = "科目名称", example = "高一")
    private String subjectName;

    @ApiModelProperty(value = "班级不合格数据")
    private List<GradeFlunkDetailResModel> details;
}