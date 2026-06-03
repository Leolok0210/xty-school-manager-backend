package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 活动匹配列表响应模型
 */
@Data
@ApiModel("活动匹配列表响应模型")
public class ActivityStudentReportListResModel {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("学生姓名")
    private String studentName;

    @ApiModelProperty("班级名称")
    private String className;

    @ApiModelProperty("级组名称")
    private String gradeGroupName;

    @ApiModelProperty("学生编号")
    private String studentNo;

    @ApiModelProperty("第几志愿数")
    private Long volunteerType;

    @ApiModelProperty("状态（1.匹配 2.发布）")
    private Integer status;

    @ApiModelProperty("学生id")
    private Long studentId;

    /**
     * 类型（1.预先导入 2.分配，3 一次报名志愿录入，4 二次报名志愿录入）
     */
    @ApiModelProperty("类型（1.预先导入 2.分配，3 一次报名志愿录入，4 二次报名志愿录入）")
    private Integer type;
} 