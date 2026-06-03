package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("考勤分页查询参数")
public class TeacherAttendancePageReqModel extends PageReqModel {
    @NotNull(message = "学校id不能为空")
    @ApiModelProperty(value = "学校id")
    private Long schoolId;

    @ApiModelProperty(value = "教师ID")
    private Long teacherId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "开始日期", example = "2023-10-01")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "结束日期", example = "2023-10-31")
    private LocalDate endDate;

    @Range(min = 1, max = 4, message = "状态值不合法")
    @ApiModelProperty(value = "状态 1-正常 2-迟到 3-早退 4-缺卡")
    private Integer status;
} 