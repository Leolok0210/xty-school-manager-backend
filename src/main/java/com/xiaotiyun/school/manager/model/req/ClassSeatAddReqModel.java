package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("班级座位添加请求信息")
public class ClassSeatAddReqModel {
    @ApiModelProperty("所属学年")
    private String sid; // 修改: Long 改为 String

    @NotNull(message = "班级id不能为空")
    @ApiModelProperty("班级id")
    private Long classId;

    @NotNull(message = "座位号不能为空")
    @ApiModelProperty("座位号")
    private Integer seatNumber;

    @NotNull(message = "学生id不能为空")
    @ApiModelProperty("学生id")
    private Long studentId;

    @ApiModelProperty("学校ID")
    private Long schoolId;
}