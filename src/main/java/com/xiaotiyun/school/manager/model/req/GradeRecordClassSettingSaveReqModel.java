package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.*;
import java.util.List;

@Data
@ApiModel("成绩录入班级设定保存请求")
public class GradeRecordClassSettingSaveReqModel {
    
    @NotBlank(message = "学年不能为空")
    @ApiModelProperty(value = "学年", required = true, example = "2025-2026")
    private String schoolYear;
    
    @NotEmpty(message = "班级设定不能为空")
    @ApiModelProperty(value = "班级设定列表", required = true)
    private List<ClassSetting> classSettings;
    
    @Data
    @ApiModel("班级设定")
    public static class ClassSetting {
        @NotNull(message = "班级ID不能为空")
        @ApiModelProperty(value = "班级ID", required = true)
        private Long classId;
        
        @ApiModelProperty(value = "是否可录入考试成绩", required = true)
        private Boolean canRecordExam;
        
        @ApiModelProperty(value = "是否可录入毕业成绩", required = true)
        private Boolean canRecordGraduation;
    }
} 