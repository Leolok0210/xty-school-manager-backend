package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("平时成绩类型响应模型")
public class StudentUsuallyTypeResModel {

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private Long id;

    /**
     * 学校ID
     */
    @ApiModelProperty(value = "学校ID")
    private Long schoolId;

    /**
     * 平时成绩类型名称
     */
    @ApiModelProperty(value = "平时成绩类型名称")
    private String typeName;
}
