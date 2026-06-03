package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 校外活动奖项评级表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("external_competition_awards")
public class ExternalCompetitionAwardsEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 学校ID
     */
    @ApiModelProperty(value = "学校ID")
    private Long schoolId;

    /**
     * 奖项评级名称
     */
    @ApiModelProperty(value = "奖项评级名称")
    private String awardsName;
}
