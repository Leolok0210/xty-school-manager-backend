package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_file_relevance")
public class SysFileRelevanceEntity extends BaseEntity {

    private Long fileId;

    private Integer type;//文件业务类型，5-学生请假附件

    private Long businessId;

    private Long schoolId;
}