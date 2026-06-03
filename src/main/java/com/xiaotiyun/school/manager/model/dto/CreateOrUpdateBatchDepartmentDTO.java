package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

@Data
public class CreateOrUpdateBatchDepartmentDTO {

    private Long relId;

    private String wxId;

    private String parentId;
}
