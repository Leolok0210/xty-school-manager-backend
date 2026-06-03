package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "学校信息返回模型", description = "包含学校和用户在本校基本信息的返回数据模型")
public class SchoolInfoResModel {
    /**
     * 学校ID
     */
    @ApiModelProperty(value = "学校ID", example = "", required = true, position = 1)
    private Long schoolId;
    
    /**
     * 学校名称
     */
    @ApiModelProperty(value = "学校名称", example = "", required = true, position = 2)
    private String schoolName;
    
    /**
     * 剩余过期天数(null表示永久有效)
     */
    @ApiModelProperty(value = "剩余过期天数(null表示永久有效，0表示已过期)", example = "", position = 3)
    private Integer remainDays;
}