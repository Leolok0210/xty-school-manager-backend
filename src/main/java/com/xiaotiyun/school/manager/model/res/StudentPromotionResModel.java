package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("升留级登记响应参数")
public class StudentPromotionResModel {
    @ApiModelProperty("记录ID")
    private Long id;

    @ApiModelProperty("学年")
    private String schoolYear;

    @ApiModelProperty("学生姓名")
    private String studentName;

    @ApiModelProperty("级组名称")
    private String gradeName;

    @ApiModelProperty("班级名称")
    private String className;

    @ApiModelProperty("班内号")
    private Integer seatNo;

    @ApiModelProperty("类型（1-升级 2-留级 3-带科）")
    private Integer promotionType;
}