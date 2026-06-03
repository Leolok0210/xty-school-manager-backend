package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(description = "企业微信同步任务对象")
public class EnterpriseWechatSynResModel {

    /**
     * 学校id
     */
    @ApiModelProperty(value = "学校id", required = true)
    private Long schoolId;

    /**
     * 类型（1-级组 2-班级 3-学生 4-家长）
     */
    @ApiModelProperty(value = "类型（1-级组 2-班级 3-学生 4-家长）", required = true)
    private Integer type;

    /**
     * 总记录数
     */
    @ApiModelProperty(value = "总记录数", required = true)
    private Integer totalCount;

    /**
     * 成功记录数
     */
    @ApiModelProperty(value = "成功记录数", required = true)
    private Integer successCount;

    /**
     * 失败记录数
     */
    @ApiModelProperty(value = "失败记录数", required = true)
    private Integer failCount;

    /**
     * 状态(0:待导入,1:导入中,2:已处理)
     */
    @ApiModelProperty(value = "状态(0:待导入,1:导入中,2:已处理)", required = true)
    private Integer status;

    /**
     *
     */
    @ApiModelProperty(value = "", required = true)
    private Long opUserId;

    /**
     * 操作人名称
     */
    @ApiModelProperty(value = "操作人名称", required = true)
    private String opUserName;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间", required = true)
    private Date startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间", required = true)
    private Date endTime;
}
