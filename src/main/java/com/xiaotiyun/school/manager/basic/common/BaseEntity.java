package com.xiaotiyun.school.manager.basic.common;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数据库实体类基类
 * 逻辑删除说明:
 * deleted=0表示未删除,deleted=记录ID表示已删除(防止唯一索引冲突)
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除(0:未删除 其他:已删除)
     */
    @TableLogic(value = "0", delval = "id")
    private Long deleted;
} 