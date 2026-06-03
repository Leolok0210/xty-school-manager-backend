package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 企业微信关联同步表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("enterprise_wechat_syn")
public class EnterpriseWechatSynEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;


    /**
     * 学校id
     */
    @TableField("school_id")
    private Long schoolId;

    /**
     * 类型（1-级组 2-班级 3-学生 4-家长）
     */

    @TableField("type")
    private Integer type;

    /**
     * 总记录数
     */

    @TableField("total_count")
    private Integer totalCount;

    /**
     * 成功记录数
     */

    @TableField("success_count")
    private Integer successCount;

    /**
     * 失败记录数
     */

    @TableField("fail_count")
    private Integer failCount;

    /**
     * 状态(0:待导入,1:导入中,2:已处理)
     */

    @TableField("status")
    private Integer status;

    /**
     * 
     */

    @TableField("op_user_id")
    private Long opUserId;

    /**
     * 开始时间
     */

    @TableField("start_time")
    private java.util.Date startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private java.util.Date endTime;


}