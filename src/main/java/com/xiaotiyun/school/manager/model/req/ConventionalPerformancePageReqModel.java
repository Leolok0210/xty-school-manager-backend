package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConventionalPerformancePageReqModel extends PageReqModel {
    @NotBlank(message = "学年不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "学年格式不正确")
    @ApiModelProperty(value = "学年", example = "2023-2024", required = true)
    private String sid;
    @ApiModelProperty(value = "学期id")
    private Long term;
    @ApiModelProperty("学部(1:幼稚园 2:小学 3:中学)")
    private Integer department;
    @ApiModelProperty(value = "班级id")
    private Long classId;
    @ApiModelProperty(value = "学生ID")
    private Long studentId;
    @ApiModelProperty(value = "类型(1.上课违规;2.欠作业;3.仪表不符;5.欠课本;7.欠回条)")
    private Integer type;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "事件开始日期")
    private LocalDate eventStartDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "事件结束日期")
    private LocalDate eventEndDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "登记开始日期")
    private LocalDate createStartDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "登记结束日期")
    private LocalDate createEndDate;
}