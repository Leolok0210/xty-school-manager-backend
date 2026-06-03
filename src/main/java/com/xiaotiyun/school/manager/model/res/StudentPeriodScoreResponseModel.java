package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "学生平时成绩响应信息")
public class StudentPeriodScoreResponseModel {

    @ApiModelProperty("学生ID")
    private Long studentId;

    @ApiModelProperty("学段ID")
    private Long periodId;

    @ApiModelProperty("科目ID")
    private Long subjectId; // 新增科目ID字段

    @ApiModelProperty("成绩*100")
    private Integer score;

    @ApiModelProperty("测验类型ID")
    private Long typeId;
}
