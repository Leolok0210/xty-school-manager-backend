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
@ApiModel("平时成绩分页查询参数")
public class StudentUsuallyPageReqModel extends PageReqModel {
    @NotNull(message = "学校ID不能为空")
    @ApiModelProperty(value = "学校id", required = true)
    private Long schoolId;
    @ApiModelProperty("学年")
    private String schoolYear;
    @ApiModelProperty("学部(1:幼稚园 2:小学 3:中学)")
    private Integer department;
    @ApiModelProperty("学段id")
    private Long periodId;
    @ApiModelProperty("科目id")
    private Long subjectId;
    @ApiModelProperty("级组id")
    private Long gradeId;
    @ApiModelProperty("班级id")
    private Long classId;
    @ApiModelProperty("学生信息")
    private String studentInfo;
    @ApiModelProperty("测验名称")
    private String name;
    @ApiModelProperty("开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;
    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;

    private Long userId;

    private List<Long> classIds;
}