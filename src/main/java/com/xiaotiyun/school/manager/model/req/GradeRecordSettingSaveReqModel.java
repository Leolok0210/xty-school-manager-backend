package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("成绩录入设定保存请求")
public class GradeRecordSettingSaveReqModel {
    
    @NotBlank(message = "学年不能为空")
    @ApiModelProperty(value = "学年", required = true, example = "2024-2025")
    private String schoolYear;
    
    @Valid
    @ApiModelProperty(value = "时间设定列表")
    private List<TimeSettingItem> timeSettings;
    
    @Valid
    @ApiModelProperty(value = "班级设定列表")
    private List<ClassSettingItem> classSettings;
    
    @Data
    @ApiModel("时间设定项")
    public static class TimeSettingItem {
        @ApiModelProperty(value = "设定ID(新增时不传,修改时必传)")
        private Long id;
        
        @NotNull(message = "学段ID不能为空")
        @ApiModelProperty(value = "学段ID", required = true)
        private Long semesterId;
        
        @NotNull(message = "学部不能为空")
        @Min(value = 1, message = "学部值必须在1-3之间")
        @Max(value = 3, message = "学部值必须在1-3之间")
        @ApiModelProperty(value = "学部(1:幼稚园 2:小学 3:中学)", required = true, example = "1")
        private Integer department;
        
        @ApiModelProperty(value = "开始时间")
        private LocalDateTime startTime;
        
        @ApiModelProperty(value = "结束时间")
        private LocalDateTime endTime;
    }
    
    @Data
    @ApiModel("班级设定")
    public static class ClassSettingItem {
        @NotNull(message = "班级ID不能为空")
        @ApiModelProperty(value = "班级ID", required = true)
        private Long classId;

        @NotNull(message = "级组ID不能为空")
        @ApiModelProperty(value = "级组ID", required = true)
        private Long gradeId;
        
        @ApiModelProperty(value = "是否可录入考试成绩", required = true)
        private Boolean canRecordExam;
        
        @ApiModelProperty(value = "是否可录入毕业成绩", required = true)
        private Boolean canRecordGraduation;

        @ApiModelProperty(value = "是否可录入德育", required = true)
        private Boolean canRecordMoralEducation;

        @ApiModelProperty(value = "是否可录入义工", required = true)
        private Boolean canRecordVolunteer;

        @ApiModelProperty(value = "是否可录入操行", required = true)
        private Boolean canRecordConduct;
    }
} 