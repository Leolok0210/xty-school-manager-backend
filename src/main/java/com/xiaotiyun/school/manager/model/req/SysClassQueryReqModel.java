package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("班级查询请求信息")
public class SysClassQueryReqModel {
    @ApiModelProperty(value = "页码", required = true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页大小",required = true)
    private Integer pageSize;

    @ApiModelProperty("级组")
    private Long gradeGroup;

    @ApiModelProperty("班级序号")
    private Integer classSerialNumber;

    @ApiModelProperty("班级id")
    private Long classId;

    @ApiModelProperty("是否专业版 (0. 否，1. 是)")
    private Integer professionalVersion;

    @ApiModelProperty("文理科（仅标识）1文科 2理科")
    private Integer artsScience;

    @ApiModelProperty("专业")
    private String professional;
    @ApiModelProperty(value = "学年",required = true)
    private String sid;

    @NotNull(message = "学校ID不能为空")
    @ApiModelProperty(value = "学校ID",required = true)
    private Long schoolId;

    @ApiModelProperty("班级编号")
    private String classNumber;


    private Long userId;


}