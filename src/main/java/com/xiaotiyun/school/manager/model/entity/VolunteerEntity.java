package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("volunteer_service")
@ApiModel("义工服务实体")
public class VolunteerEntity extends BaseEntity {
    @ApiModelProperty(value = "学校ID", required = true, example = "1001")
    private Long schoolId;
    
    @ApiModelProperty(value = "学年", required = true, example = "2023-2024", 
        notes = "格式为YYYY-YYYY，如2023-2024表示2023至2024学年")
    private String schoolYear;
    
    @ApiModelProperty(value = "班级ID", required = true, example = "201")
    private Long classId;

    @ApiModelProperty(value = "级组名称", required = true)
    private String gradeName;

    @ApiModelProperty(value = "班级名称", required = true)
    private String className;

    @ApiModelProperty(value = "学生ID", required = true, example = "10001")
    private Long studentId;
    
    @ApiModelProperty(value = "活动名称", required = true, example = "社区环保活动")
    private String activityName;
    
    @ApiModelProperty(value = "机构名称", required = true, example = "绿色环保协会")
    private String organization;
    
    @ApiModelProperty(value = "服务日期", required = true, example = "2023-09-15")
    private LocalDate serviceDate;
    
    @ApiModelProperty(value = "开始时间", required = true, example = "09:00:00")
    private LocalTime startTime;
    
    @ApiModelProperty(value = "结束时间", required = true, example = "12:30:00")
    private LocalTime endTime;
    
    @ApiModelProperty(value = "服务秒数", required = true, example = "12600", 
        notes = "根据开始时间和结束时间计算得出，单位：秒")
    private Long serviceSeconds;
    
    @ApiModelProperty(value = "服务性质", example = "公益服务", 
        notes = "如：公益服务、社区服务等")
    private String serviceNature;
    
    @ApiModelProperty(value = "服务类别", example = "环保类", 
        notes = "如：环保类、教育类、医疗类等")
    private String serviceType;
} 