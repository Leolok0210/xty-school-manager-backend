package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("级组查询请求信息")
public class GradeGroupQueryReqModel {

    @ApiModelProperty("id")
    private List<Long> ids;

    @ApiModelProperty("学部（1:幼稚园 2:小学 3:中学）")
    private Long department;

    @ApiModelProperty("级组名称")
    private String gradeGroupName;

    @ApiModelProperty(value = "学校ID",required = true)
    private Long schoolId;

    @ApiModelProperty("级组")
    private String grade;

    @ApiModelProperty(value = "学年",required = true)
    private String sid;

    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码最小为1")
    @ApiModelProperty(value = "页码",required = true)
    private Integer pageNum;

    @NotNull(message = "每页大小不能为空")
    @Min(value = 1, message = "每页大小最小为1")
    @ApiModelProperty(value = "每页大小",required = true)
    private Integer pageSize;
}