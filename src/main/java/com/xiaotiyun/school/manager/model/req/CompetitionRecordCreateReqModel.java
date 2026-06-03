package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.xiaotiyun.school.manager.basic.validation.AtLeastOnePositive;

@Data
@ApiModel("参赛记录创建参数")
@AtLeastOnePositive(
    fields = {"meritBig", "meritSmall", "meritAdvantage", "demeritBig", "demeritSmall", "demeritShortcoming"},
    message = "至少需要填写一个功过类型"
)
public class CompetitionRecordCreateReqModel {
    @ApiModelProperty(value = "学生ID", required = true)
    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @ApiModelProperty(value = "班级ID", required = true)
    @NotNull(message = "班级ID不能为空")
    private Long classId;

    @ApiModelProperty("比赛奖励")
    @Size(max = 50, message = "奖励信息最多50个字符")
    private String award;
    
    @ApiModelProperty("大功次数")
    @Min(value = 0, message = "次数不能小于0")
    private Integer meritBig;
    
    @ApiModelProperty("小功次数")
    @Min(0)
    private Integer meritSmall;
    
    @ApiModelProperty("优点次数")
    @Min(0)
    private Integer meritAdvantage;
    
    @ApiModelProperty("大过次数")
    @Min(0)
    private Integer demeritBig;
    
    @ApiModelProperty("小过次数")
    @Min(0)
    private Integer demeritSmall;
    
    @ApiModelProperty("缺点次数")
    @Min(0)
    private Integer demeritShortcoming;
} 