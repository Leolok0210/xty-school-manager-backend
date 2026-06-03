package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 余暇活动课程记录响应数据模型
 */
@Data
@ApiModel("余暇活动课程记录响应数据")
public class LeisureActivityCoursesRecordRes {

    /**
     * 主键ID
     */
    @ApiModelProperty("主键ID")
    private Long id;

    /**
     * 学校ID
     */
    @ApiModelProperty("学校ID")
    private Long schoolId;

    /**
     * 活动ID
     */
    @ApiModelProperty("活动ID")
    private Long activityId;

    /**
     * 课程名称
     */
    @ApiModelProperty("课程名称")
    private String name;

    /**
     * 教师名称
     */
    @ApiModelProperty("教师名称")
    private String teacher;

    /**
     * 教师ID
     */
    @ApiModelProperty("教师ID")
    private Long teacherId;

    /**
     * 教室名称
     */
    @ApiModelProperty("教室名称")
    private String classroom;

    /**
     * 教室ID，为空时为自定义地址
     */
    @ApiModelProperty("教室ID，为-1时为自定义地址")
    private Long classroomId;

    /**
     * 课程名额
     */
    @ApiModelProperty("课程名额")
    private Integer quotaTotal;

    /**
     * 课程次数
     */
    @ApiModelProperty("课程次数")
    private Integer coursesNum;

    /**
     * 活动状态(0-不开放 1-开放)
     */
    @ApiModelProperty("活动状态(0-不开放 1-开放)")
    private Integer status;

    /**
     * 上课时间，JSON格式，每条记录包含周几、开始时间和结束时间
     */
    @ApiModelProperty("上课时间，JSON格式，每条记录包含周几、开始时间和结束时间")
    private String courseTime;

    /**
     * 报名人数
     */
    @ApiModelProperty("报名人数/预先导入名单")
    private Integer enrollNum;

    /**
     * 一次报名人数
     */
    @ApiModelProperty("一次报名人数")
    private Integer oneEnrollNum;

    /**
     * 二次报名人数
     */
    @ApiModelProperty("二次报名人数")
    private Integer twoEnrollNum;

    /**
     * 录取状态
     */
    @ApiModelProperty("录取状态, 0-未录满, 1-已录满")
    private Integer enrollStatus;

    /**
     * 可报名额=课程名额-录取人数
     */
    @ApiModelProperty("可报名额")
    private Integer enrollQuota;

    /**
     * 录取人数
     */
    @ApiModelProperty("录取人数")
    private Integer reportQuota;
}

