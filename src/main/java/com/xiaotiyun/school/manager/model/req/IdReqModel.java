package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class IdReqModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "id", required = true)
    private Long id;
}