package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("transcript_details")
public class TranscriptDetailsEntity extends BaseEntity {
    
    private Long studentId;
    
    private Long schoolId;
    
    private Long classId;
    
    private String imgUrl;
    
    private String pdfUrl;
    
    private String schoolYear;
} 