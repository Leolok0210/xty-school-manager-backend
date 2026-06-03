package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("teacher_business")
@ApiModel(value = "教师公务实体")
public class TeacherBusinessEntity extends BaseEntity {
    
    @ApiModelProperty(value = "学校ID", required = true)
    private Long schoolId;
    
    @ApiModelProperty(value = "教师ID", required = true)
    private Long teacherId;
    
    @ApiModelProperty(value = "开始时间", required = true)
    private LocalDateTime startTime;
    
    @ApiModelProperty(value = "结束时间", required = true)
    private LocalDateTime endTime;
    
    @ApiModelProperty(value = "公务事由", required = true)
    private String reason;
} 