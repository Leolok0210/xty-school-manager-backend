package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;

/**
 * 义工服务分页查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("义工服务分页查询参数")
public class VolunteerPageReqModel extends PageReqModel {
    @NotNull(message = "学校ID不能为空")
    @ApiModelProperty(value = "学校ID", example = "1001")
    private Long schoolId;
    
    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "学年格式不正确")
    @ApiModelProperty(value = "学年", example = "2023-2024")
    private String schoolYear;
    
    @ApiModelProperty(value = "班级ID", example = "201")
    private Long classId;

    @ApiModelProperty(value = "学生ID", example = "10001")
    private Long studentId;

    @ApiModelProperty(value = "活动名称（模糊查询）", example = "环保")
    private String activityName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "服务日期范围开始", example = "2023-09-01")
    private LocalDate serviceDateStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "服务日期范围结束", example = "2023-09-30")
    private LocalDate serviceDateEnd;

    private Long userId;
    private List<Long> classIds;

    @ApiModelProperty(value = "学段ID", example = "1")
    private Long semesterId;
}