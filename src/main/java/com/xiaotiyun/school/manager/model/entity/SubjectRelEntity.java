package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 科目表关联表
 */
@Data
@TableName("subject_rel")
public class SubjectRelEntity extends BaseEntity {
    /** 级组id */
    @TableField("group_id")
    private Long groupId;

    /** 科目id */
    @TableField("subject_id")
    private Long subjectId;

    /** 序号 */
    @TableField("number")
    private Integer number;

    /** 是否计入平均分 (0. 否，1. 是) */
    @TableField("counted_in_average")
    private Integer countedInAverage;

    /** 文科理科：0-公共，1-文科，2-理科 */
    @TableField("arts_science")
    private Integer artsScience;

    /** 1-选修 2-必修 */
    @TableField("subject_type")
    private Integer subjectType;

    /** 学校ID */
    @TableField("school_id")
    private Long schoolId;

    /** 成绩展示规则，0-分数，1-评级 */
    @TableField("show_rule")
    private Integer showRule;

    // createTime, updateTime, deleted, id 由BaseEntity继承
} 