package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("科目关联查询请求")
public class SubjectRelQueryReqModel {
    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("级组id")
    private Long groupId;

    @ApiModelProperty("科目id")
    private Long subjectId;

    @ApiModelProperty("学校ID")
    private Long schoolId;
} 