package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("班级座位详情返回信息")
public class ClassSeatDetailResModel {
    @ApiModelProperty("ID")
    private Long id;

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

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("是否删除 (0. 否，1. 是)")
    private Integer isDeleted;
}