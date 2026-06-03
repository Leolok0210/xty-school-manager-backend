package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 校外组别记录表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("external_competition_group")
public class ExternalCompetitionGroupEntity extends BaseEntity {

    /**
     * 比赛ID
     */
    private Long competitionId;

    /**
     * 组别名称
     */
    private String groupName;
}
