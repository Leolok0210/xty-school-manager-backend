package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 活动匹配列表请求模型
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("活动匹配列表请求")
public class ActivityStudentReportListReqModel extends PageReqModel {

    @ApiModelProperty("学校ID 后台使用忽略")
    private Long schoolId;

    @ApiModelProperty("学年 后台使用忽略")
    private String schoolYear;

    @ApiModelProperty("学部 后台使用忽略")
    private Integer department;

    @ApiModelProperty("活动ID 后台使用忽略")
    private Long activityId;

    @ApiModelProperty("学部ID")
    private Long departmentId;

    @ApiModelProperty("班级ID")
    private Long classId;

    @ApiModelProperty("学生姓名")
    private String studentName;

    @ApiModelProperty("学生编号")
    private String studentNo;

    @ApiModelProperty("课程ID")
    private Long lensonId;
    
    @ApiModelProperty("类型（-1全部 1.预先导入 2.分配，3 一次报名志愿录入，4 二次报名志愿录入）")
    private Integer selectType;

    @ApiModelProperty("状态（-1 全部1.匹配 2.发布）")
    private Integer status;
} 