package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("export_header")
public class ExportHeaderEntity extends BaseEntity {

    /**
     * 学校id
     */
    private Long schoolId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 类型（1.学生资料）
     */
    private Integer type;

    /**
     * 表头json
     */
    private String header;
}