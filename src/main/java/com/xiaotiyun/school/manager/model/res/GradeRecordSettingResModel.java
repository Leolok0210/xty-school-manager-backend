package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("成绩录入设定返回")
public class GradeRecordSettingResModel {
    
    @ApiModelProperty(value = "学年")
    private String schoolYear;
    
    @ApiModelProperty(value = "时间设定列表")
    private List<TimeSettingItem> timeSettings;
    
    @ApiModelProperty(value = "班级设定列表")
    private List<ClassSettingItem> classSettings;
    
    @Data
    @ApiModel("时间设定项")
    public static class TimeSettingItem {
        @ApiModelProperty(value = "设定ID")
        private Long id;
        
        @ApiModelProperty(value = "学段ID")
        private Long semesterId;
        
        @ApiModelProperty(value = "学段名称")
        private String semesterName;
        
        @ApiModelProperty(value = "学部(1:幼稚园 2:小学 3:中学)")
        private Integer department;
        
        @ApiModelProperty(value = "开始时间")
        private LocalDateTime startTime;
        
        @ApiModelProperty(value = "结束时间")
        private LocalDateTime endTime;
    }
    
    @Data
    @ApiModel("班级设定")
    public static class ClassSettingItem {
        @ApiModelProperty(value = "班级ID")
        private Long classId;

        @ApiModelProperty(value = "级组ID")
        private Long gradeId;

        @ApiModelProperty(value = "级组名称")
        private String gradeName;

        @ApiModelProperty(value = "班级名称")
        private String className;
        
        @ApiModelProperty(value = "是否可录入考试成绩")
        private Boolean canRecordExam;
        
        @ApiModelProperty(value = "是否可录入毕业成绩")
        private Boolean canRecordGraduation;

        @ApiModelProperty(value = "是否可录入德育")
        private Boolean canRecordMoralEducation;

        @ApiModelProperty(value = "是否可录入义工")
        private Boolean canRecordVolunteer;

        @ApiModelProperty(value = "是否可录入操行")
        private Boolean canRecordConduct;
    }
} 