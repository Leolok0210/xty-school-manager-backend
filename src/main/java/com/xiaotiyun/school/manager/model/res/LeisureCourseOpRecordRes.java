package com.xiaotiyun.school.manager.model.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 余暇活动课程操作记录响应数据模型（Res）
 */
@Data
@ApiModel("余暇活动课程操作记录响应数据")
public class LeisureCourseOpRecordRes {

    /**
     * 课程ID
     */
    @ApiModelProperty("课程ID")
    private Long coursesId;

    /**
     * 学生名称
     */
    @ApiModelProperty("学生名称")
    private String studentName;

    /**
     * 学生ID
     */
    @ApiModelProperty("学生ID")
    private Long studentId;

    /**
     * 操作人ID
     */
    @ApiModelProperty("操作人ID")
    private Long operatorId;

    /**
     * 操作人名称
     */
    @ApiModelProperty("操作人名称")
    private String operatorName;

    /**
     * 操作类型(0-批量导入 1-移除 2-批量移除 3-分配 4-批量分配 5-转班 6-批量转班 7-转入 8-批量转入)
     */
    @ApiModelProperty("操作类型(0-批量导入 1-移除 2-批量移除 3-分配 4-批量分配 5-转班 6-批量转班 7-转入 8-批量转入)")
    private Integer operationType;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

}

