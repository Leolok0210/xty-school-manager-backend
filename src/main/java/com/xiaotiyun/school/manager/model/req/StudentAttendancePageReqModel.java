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
@ApiModel("学生出勤规则分页查询参数")
public class StudentAttendancePageReqModel extends PageReqModel {
    @NotNull(message = "学校ID不能为空")
    @ApiModelProperty(value = "学校ID", example = "1001")
    private Long schoolId;

    @ApiModelProperty(value = "学年")
    private String schoolYear;

    @ApiModelProperty(value = "班级id")
    private Long classId;

    @ApiModelProperty(value = "学生id")
    private Long studentId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "查询开始时间", example = "2023-09-01")
    private LocalDate queryStartDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "查询结束时间", example = "2023-09-30")
    private LocalDate queryEndDate;

    @ApiModelProperty(value = "出勤状态（0.正常;1.迟到;2.早退;3.缺卡;4.数据异常）")
    private Integer status;

    private Long userId;

    private List<Long> classIds;
}