package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("成绩单详情保存请求")
public class TranScriptDetailsSaveReqModel {

    @NotNull(message = "学生ID不能为空")
    @ApiModelProperty(value = "学生ID", required = true)
    private Long studentId;

    @NotNull(message = "班级ID不能为空")
    @ApiModelProperty(value = "班级ID", required = true)
    private Long classId;

    @ApiModelProperty("图片地址")
    private String imgUrl;

    @ApiModelProperty("PDF地址") 
    private String pdfUrl;

    @NotNull(message = "学年不能为空")
    @ApiModelProperty(value = "学年", required = true)
    private String schoolYear;
} 