package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 校外活动范畴表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("external_competition_category")
public class ExternalCompetitionCategoryEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 学校ID
     */
    @ApiModelProperty(value = "学校ID")
    private Long schoolId;

    /**
     * 范畴名称
     */
    @ApiModelProperty(value = "范畴名称")
    private String categoryName;
}
