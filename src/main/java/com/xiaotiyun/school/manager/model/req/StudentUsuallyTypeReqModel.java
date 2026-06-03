package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 平时成绩类型请求模型
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("平时成绩类型请求模型")
public class StudentUsuallyTypeReqModel extends PageReqModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 平时成绩类型名称
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "平时成绩类型名称", required = true)
    private String typeName;
}
