package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.*;

/**
 * 数据录入记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("data_operation_log")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataOperationLogEntity extends BaseEntity {

    /**
     * 业务ID，根据类型关联其他表的主键ID
     */
    private Long businessId;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 操作类型（1-新增，2-修改）
     */
    private Integer operationType;

    /**
     * 业务类型（1-平时成绩登记，2-考试成绩登记，3-毕业考试登记，4-学年素质登记，5-奖励登记，6-惩罚登记，7-课堂表现登记，8-欠交作业登记，9-仪表不符登记，10-巡堂登记，11-大息小息表现登记，12-课外比赛登记，13-校外比赛登记，14-义工服务）
     */
    private Integer businessType;
}
