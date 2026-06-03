package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户班级关联信息返回参数")
public class UserClassRelResModel {
    /**
     * 学校ID
     */
    @ApiModelProperty("学校ID")
    private Long schoolId;

    /**
     * 教师ID
     */
    @ApiModelProperty("教师ID")
    private Long userId;
    /**
     * 关联类型，1-学部，2-级组，3-班级
     */
    @ApiModelProperty("关联类型，1-学部，2-级组，3-班级")
    private Integer type;
    /**
     * 关联ID
     */
    @ApiModelProperty("类型关联ID")
    private Long relId;
}
