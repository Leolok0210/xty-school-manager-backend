package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("下载学生照片PDF请求参数")
public class StudentImagePDFReqModel {

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty("班级id")
    private Long classId;

    @Min(value = 1, message = LanguageConstants.PARAM_ERROR)
    @Max(value = 2, message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty("打印格式：1.班内号+姓名 2.班内号+姓名+联络电话")
    private Integer formatType;
}
