package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("课堂表现查询请求信息")
public class ClassPerformanceQueryReqModel {
    @ApiModelProperty(value = "页码",required = true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页大小",required = true)
    private Integer pageSize;

    @ApiModelProperty(value = "所属学年",required = true)
    private String sid;

    @ApiModelProperty(value = "学校ID",required = true)
    private Long schoolId;

    @ApiModelProperty(value = "学期",required = true)
    private Long term;

    @ApiModelProperty("学生studentName")
    private String studentName;

    @ApiModelProperty("班级id")
    private Long classId;

    //时间

    @ApiModelProperty("开始日期")
    private LocalDateTime startDate;
    @ApiModelProperty("结束日期")
    private LocalDateTime endDate;

    private Long userId;
    private List<Long> classIds;
}