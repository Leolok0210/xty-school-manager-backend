package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDate;

@Data
@ApiModel("校历事项响应信息")
public class SchoolCalendarEventResModel {
    @ApiModelProperty("事项ID")
    private Long id;

    @ApiModelProperty("事项类型(1:教师假期,2:考试,3:活动,4:其他,5:学生假期)")
    private Integer eventType;
    
    @ApiModelProperty("事项日期")
    private LocalDate eventDate;

    @ApiModelProperty("事项描述(系统生成的事项需支持国际化，格式为【{\"zh-MO\": \"中華人民共和國國慶日\", \"en-US\": \"National Day of China\", \"pt-PT\": \"Dia Nacional da China\"}】)")
    private String eventDescription;

    @ApiModelProperty("是否系统生成")
    private Boolean isSystem;
}