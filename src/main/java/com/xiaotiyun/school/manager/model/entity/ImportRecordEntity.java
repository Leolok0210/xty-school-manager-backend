package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("import_record")
public class ImportRecordEntity extends BaseEntity {

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 错误行号/照片名称
     */
    private String incorrectLineno;

    /**
     * 错误原因
     */
    private String incorrectReason;
}