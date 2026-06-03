package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserRewardCountReqModel {
    @ApiModelProperty(value = "类型 1奖励 2惩罚", required = true)
    private Integer type;

    @ApiModelProperty(value = "学年", required = true)
    private String sid;

    @ApiModelProperty(value = "学校ID", required = true)
    private Long schoolId;

    @ApiModelProperty(value = "学生ID列表", required = true)
    private List<Long> studentIds;
    //学段id
    @ApiModelProperty(value = "学段ID")
    private Long termId;
}