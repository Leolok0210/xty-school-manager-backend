package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("欠交作业表现查询请求信息")
public class MissingAssignmentPerformanceQueryReqModel {
    @ApiModelProperty("页码")
    private Integer pageNum;

    @ApiModelProperty("每页大小")
    private Integer pageSize;

    @ApiModelProperty("所属学年")
    private String sid;

    @ApiModelProperty("学期")
    private Long term;

    @ApiModelProperty("班级id")
    private Long classId;

    @ApiModelProperty("学校ID")
    private Long schoolId;

    @ApiModelProperty("学生id")
    private Long studentId;

    @ApiModelProperty("科目")
    private Long subjectId;

    @ApiModelProperty("学生姓名")
    private String studentName;

    @ApiModelProperty("开始日期")
    private LocalDateTime startDate;
    @ApiModelProperty("结束日期")
    private LocalDateTime endDate;

    private Long userId;

    private List<Long> classIds;
}