package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("教师公务请求参数")
public class TeacherBusinessPageReqModel extends PageReqModel {
    @NotNull(message = "学校id不能为空")
    @ApiModelProperty(value = "学校id", required = true)
    private Long schoolId;

    @ApiModelProperty(value = "教师id")
    private Long teacherId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "开始日期", example = "2023-10-01")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "结束日期", example = "2023-10-31")
    private LocalDate endDate;
}