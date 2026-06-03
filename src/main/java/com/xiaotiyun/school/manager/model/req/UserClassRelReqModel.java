package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("用户班级关联表")
public class UserClassRelReqModel {
    /**
     * 学校ID
     */
    @ApiModelProperty("学校ID")
    private Long schoolId;

    /**
     * 用户ID
     */
    @ApiModelProperty("教师ID")
    private Long userId;
    /**
     * 关联类型，1-学部，2-级组，3-班级
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty("关联类型，0-全部，1-学部，2-级组，3-班级")
    private Integer type;
    /**
     * 关联ID
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty("关联ID,什么类型就传什么ID，类型为全部时传0")
    private Long relId;
}
