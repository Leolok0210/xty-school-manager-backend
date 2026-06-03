package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("学生公务请求参数")
public class StudentBusinessPageReqModel extends PageReqModel {
    @ApiModelProperty(value = "学年")
    private String schoolYear;

    @ApiModelProperty(value = "班级id")
    private Long classId;

    @ApiModelProperty(value = "学生id")
    private Long studentId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "开始日期", example = "2023-10-01")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "结束日期", example = "2023-10-31")
    private LocalDate endDate;

    private Long userId;
    private List<Long> classIds;
}