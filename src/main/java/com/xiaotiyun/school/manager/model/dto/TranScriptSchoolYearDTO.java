package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TranScriptSchoolYearDTO {
//    @ApiModelProperty("学段ID")
    private Long periodId;

//    @ApiModelProperty("学段名称")
    private String periodName;

//    @ApiModelProperty("学段占比")
    private Integer proportion;
    //时间
    private LocalDateTime startTime;
}
