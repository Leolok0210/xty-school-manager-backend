package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Pattern;

@Data
@ApiModel("请假缺席导出参数")
public class StudentLeaveExportReqModel {
    
    @ApiModelProperty(value = "学校ID", required = true, example = "1001")
    private Long schoolId;
    
    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "学年格式不正确")
    @ApiModelProperty(value = "学年", example = "2023-2024")
    private String schoolYear;
    
    @ApiModelProperty(value = "班级ID", example = "201")
    private Long classId;
    
    @ApiModelProperty(value = "学生ID", example = "1001")
    private Long studentId;
    
    @Range(min = 1, max = 2, message = "类型值不合法")
    @ApiModelProperty(value = "类型 1-请假 2-缺席", example = "1")
    private Integer leaveType;
} 