package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@ApiModel("科目添加请求信息")
public class SubjectAddReqModel {
    @ApiModelProperty("科目ID")
    private Long id;

    @Size(max = 20, message = "科目编号不能超过20个字")
    @NotNull(message = "科目编号不能为空")
    @ApiModelProperty(value = "科目编号",required = true)
    private String subjectNumber;

    @Size(max = 20, message = "科目名称不能超过20个字")
    @ApiModelProperty("科目名称")
    private String subjectName;

    @Size(max = 20, message = "科目英文名称不能超过20个字")
    @ApiModelProperty("科目英文名称")
    private String subjectEnglishName;

    @ApiModelProperty("单位")
    private Integer unit;


    @NotNull(message = "范围不能为空")
    @ApiModelProperty("范围 格式[1,2,3]")
    private String scope;

}