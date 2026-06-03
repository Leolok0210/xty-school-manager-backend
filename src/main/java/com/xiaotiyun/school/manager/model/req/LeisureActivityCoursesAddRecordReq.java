package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.*;

/**
 * 余暇活动课程记录请求参数模型
 */
@Data
@ApiModel("余暇活动课程记录请求参数")
public class LeisureActivityCoursesAddRecordReq {

    /**
     * id
     */
    @ApiModelProperty(value = "主键id，更新时必传")
    private Long id;

    /**
     * 学校ID
     */
    @ApiModelProperty(value = "学校ID", required = true)
    private Long schoolId;

    /**
     * 活动ID
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty("活动ID")
    private Long activityId;

    /**
     * 课程名称
     */
    @NotBlank(message = LanguageConstants.PARAM_ERROR)
    @Size(max = 20, message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "课程名称", required = true)
    private String name;

    /**
     * 教师名称
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty("教师名称")
    private String teacher;

    /**
     * 教师ID
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty("教师ID")
    private Long teacherId;

    /**
     * 教室名称
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @Size(max = 20, message = LanguageConstants.PARAM_ERROR)
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
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty("课程名额")
    private Integer quotaTotal;

    /**
     * 课程次数
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty("课程次数")
    private Integer coursesNum;

    /**
     * 活动状态(0-不开放 1-开放)
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty("活动状态(0-不开放 1-开放)")
    private Integer status;

    /**
     * 上课时间，JSON格式，每条记录包含周几、开始时间和结束时间
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty("上课时间，JSON格式，每条记录包含周几、开始时间和结束时间，格式为[{\"week\": \"周一\", \"startTime\": \"08:00\", \"endTime\": \"10:00\"}]")
    private String courseTime;
}
