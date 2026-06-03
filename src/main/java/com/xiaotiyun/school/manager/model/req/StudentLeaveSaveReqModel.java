package com.xiaotiyun.school.manager.model.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel("请假缺席请求参数")
public class StudentLeaveSaveReqModel {
    @ApiModelProperty(value = "学校ID", required = true)
    @NotNull(message = "学校ID不能为空")
    private Long schoolId;
    
    @NotBlank(message = "学年不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "学年格式不正确")
    @ApiModelProperty(value = "学年", required = true, example = "2023-2024")
    private String schoolYear;
    
    @NotNull(message = "班级不能为空")
    @Positive(message = "班级ID必须为正数")
    @ApiModelProperty(value = "班级ID", required = true, example = "201")
    private Long classId;
    
    @NotNull(message = "学生不能为空")
    @Positive(message = "学生ID必须为正数")
    @ApiModelProperty(value = "学生ID", required = true, example = "1001")
    private Long studentId;
    
    @NotNull(message = "日期不能为空")
    @ApiModelProperty(value = "请假日期", required = true, example = "2023-10-01")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate leaveDate;
    
    @NotNull(message = "类型不能为空")
    @Range(min = 1, max = 3, message = "类型值不合法")
    @ApiModelProperty(value = "类型 1-请假 2-缺席 3-迟到", required = true, example = "1")
    private Integer leaveType;
    
    @NotNull(message = "节数不能为空")
    @Min(value = 1, message = "节数最小为1")
    @Max(value = 99, message = "节数最大为99")
    @ApiModelProperty(value = "节数", required = true, example = "2")
    private Integer periods;
    
    @Size(max = 50, message = "备注最长50个字符")
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "文件ID")
    private List<Long> fileIds;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "请假缺席课节列表", required = true)
    private List<StudentLeaveCourseSaveReqModel> courses;
} 