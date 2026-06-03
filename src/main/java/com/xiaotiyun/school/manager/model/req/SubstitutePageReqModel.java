package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class SubstitutePageReqModel extends PageReqModel {
    @ApiModelProperty("学年")
    private String schoolYear;
    @ApiModelProperty("班级id")
    private Long classId;
    @ApiModelProperty("科目ID")
    private Long subjectId;
    @ApiModelProperty("教师id")
    private Long teacherId;
    @ApiModelProperty("代课老师姓名")
    private String teacherName;
    @ApiModelProperty("开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;
    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;
}