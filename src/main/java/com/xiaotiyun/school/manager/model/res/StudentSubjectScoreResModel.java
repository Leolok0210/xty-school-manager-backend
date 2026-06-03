package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "学生科目成绩信息")
public class StudentSubjectScoreResModel {
    
    @ApiModelProperty("科目ID")
    private Long subjectId;
    
    @ApiModelProperty("成绩*100")
    private Integer score;

    @ApiModelProperty("测验类型ID")
    private Long typeId;
}