package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("campus_photo")
public class CampusPhotoEntity extends BaseEntity {
    /** 存储文件名 */
    private String fileName;
    /** 原始文件名 */
    private String originalName;
    /** 文件大小 */
    private Long fileSize;
    /** 学校ID */
    private Long schoolId;
}
