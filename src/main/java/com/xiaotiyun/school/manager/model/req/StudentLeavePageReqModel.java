package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("请假缺席分页查询参数")
public class StudentLeavePageReqModel extends PageReqModel {
    @NotNull(message = "学校id不能为空")
    @ApiModelProperty(value = "学校ID", required = true, example = "1001")
    private Long schoolId;

    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "学年格式不正确")
    @ApiModelProperty(value = "学年", example = "2023-2024")
    private String schoolYear;

    @ApiModelProperty(value = "班级ID", example = "201")
    private Long classId;

    @ApiModelProperty(value = "班级ID数组", example = "[201]")
    private List<Long> classIds;

    @ApiModelProperty(value = "学生ID", example = "1001")
    private Long studentId;

    @Range(min = 1, max = 3, message = "类型值不合法")
    @ApiModelProperty(value = "类型 1-请假 2-缺席 3-迟到", example = "1")
    private Integer leaveType;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "日期")
    private LocalDate leaveDate;

    private Long userId;

    private List<Long> classIdList;
}