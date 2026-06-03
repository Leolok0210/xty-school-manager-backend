package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@ApiModel("教师公务请求参数")
public class TeacherBusinessSaveReqModel {
    @NotNull(message = "学校ID不能为空")
    @ApiModelProperty(value = "学校ID", required = true)
    private Long schoolId;
    
    @NotNull(message = "教师ID不能为空")
    @ApiModelProperty(value = "教师ID", required = true)
    private Long teacherId;
    
    @NotBlank(message = "公务事由不能为空")
    @Size(max = 200, message = "公务事由最长200个字符")
    @ApiModelProperty(value = "公务事由", required = true)
    private String reason;
    
    @NotNull(message = "开始时间不能为空")
    @ApiModelProperty(value = "开始时间", required = true)
    private LocalDateTime startTime;
    
    @NotNull(message = "结束时间不能为空")
    @ApiModelProperty(value = "结束时间", required = true)
    private LocalDateTime endTime;
} 