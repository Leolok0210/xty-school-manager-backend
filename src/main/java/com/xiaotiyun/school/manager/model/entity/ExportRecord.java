package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 导出记录表
 * @TableName export_record
 */
@TableName(value ="export_record")
@Data
public class ExportRecord implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 学校id
     */
    private Long schoolId;

    /**
     * 类型 1-惩罚登记
     */
    private Integer type;

    /**
     * 导出文件
     */
    private String url;

    /**
     * 关联id
     */
    private Long relId;

    /**
     * 
     */
    private Date startTime;

    /**
     * 
     */
    private Date endTime;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 
     */
    private Long deleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}