package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ApiModel("课堂表现添加请求信息")
public class ClassPerformanceAddReqModel {
    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty(value = "所属学年",required = true)
    private String sid;

    @ApiModelProperty(value = "学校ID",required = true)
    private Long schoolId;

    @NotNull(message = LanguageConstants.CLASS_ID_REQUIRED)
    @ApiModelProperty(value = "班级id",required = true)
    private Long classId;

    @NotNull(message = LanguageConstants.TERM_REQUIRED)
    @ApiModelProperty(value = "学段",required = true)
    private Long term;

    @NotNull(message = LanguageConstants.STUDENT_ID_REQUIRED)
    @ApiModelProperty(value = "学生id",required = true)
    private Long studentId;

    @NotNull(message = LanguageConstants.CLASS_DATE_REQUIRED)
    @ApiModelProperty(value = "上课日期",required = true)
    private LocalDateTime classDate;

//    @NotNull(message = LanguageConstants.CLASS_SECTION_REQUIRED)
    @ApiModelProperty(value = "节数",required = true)
    private String classSection;

    @NotBlank(message = LanguageConstants.PERFORMANCE_REQUIRED)
    @ApiModelProperty(value = "课堂表现",required = true)
    private String performance;

    @ApiModelProperty(value = "课堂表现ID,预设表中ID")
    private String performanceId;

    @NotNull(message = LanguageConstants.USER_ID_REQUIRED)
    @ApiModelProperty(value = "登记人",required = true)
    private Long userId;
}