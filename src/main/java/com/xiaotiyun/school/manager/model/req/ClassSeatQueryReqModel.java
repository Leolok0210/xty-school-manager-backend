package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("班级座位查询请求信息")
public class ClassSeatQueryReqModel {
    @ApiModelProperty("页码")
    private Integer pageNum;

    @ApiModelProperty("每页大小")
    private Integer pageSize;

    @ApiModelProperty("所属学年")
    private String sid; // 修改: Long 改为 String

    @ApiModelProperty("班级id")
    private Long classId;

    @ApiModelProperty("座位号")
    private Integer seatNumber;

    @ApiModelProperty("学生id")
    private Long studentId;

    @ApiModelProperty("学校ID")
    private Long schoolId;

    @ApiModelProperty("是否删除 (0. 否，1. 是)")
    private Integer isDeleted;
}