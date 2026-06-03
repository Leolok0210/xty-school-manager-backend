package com.xiaotiyun.school.manager.model.res;


import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "企业微信同步任务记录对象")
@Data
public class EnterpriseWechatSynRecordResModel {

    /**
     * 任务id
     */
    @ApiModelProperty("任务id")
    private Long taskId;

    /**
     * 关联id
     */
    @ApiModelProperty("关联id")
    private Long relId;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String name;

    /**
     * 错误原因
     */
    @ApiModelProperty("错误原因")
    private String incorrectReason;
}
