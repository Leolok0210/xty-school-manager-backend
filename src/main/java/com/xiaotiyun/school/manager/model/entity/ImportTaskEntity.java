package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("import_task")
public class ImportTaskEntity extends BaseEntity {

    /**
     * 学校id
     */
    private Long schoolId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * 类型（1.学生资料；2.学生照片）
     */
    private Integer type;

    /**
     * 总记录数
     */
    private Integer totalCount;

    /**
     * 成功记录数
     */
    private Integer successCount;

    /**
     * 失败记录数
     */
    private Integer failCount;

    /**
     * 状态(0:导入中,1:导入成功,2:导入失败)
     */
    private Integer status;
}