package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class StudentExamPageReqModel extends PageReqModel {
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
    @ApiModelProperty("考试名称")
    private String name;

    private Long userId;

    private List<Long> classIds;
}