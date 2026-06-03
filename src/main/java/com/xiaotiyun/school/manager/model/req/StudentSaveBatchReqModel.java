package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StudentSaveBatchReqModel {
    private Long id;
    @ApiModelProperty("座位号")
    private Integer seatNo;
}
