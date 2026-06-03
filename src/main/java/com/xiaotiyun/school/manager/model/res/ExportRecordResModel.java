package com.xiaotiyun.school.manager.model.res;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 导出记录表
 * @TableName export_record
 */
@Data
public class ExportRecordResModel implements Serializable {
    /**
     * 
     */
    private Long id;

    /**
     * 学校id
     */
    @ApiModelProperty(value = "学校id")
    private Long schoolId;

    /**
     * 类型 1-惩罚登记
     */
    @ApiModelProperty(value = "类型 1-惩罚登记")
    private Integer type;

    /**
     * 导出文件
     */
    @ApiModelProperty(value = "导出文件")
    private String url;

    /**
     * 关联id
     */
    @ApiModelProperty(value = "关联id")
    private Long relId;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /**
     * 
     */
    @ApiModelProperty(value = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}