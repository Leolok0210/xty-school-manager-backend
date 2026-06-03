package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

@Data
public class StudentImageDownloadDTO {
    /**
     * 图片路径
     */
    private String imgUrl;
    /**
     * 图片名称
     */
    private String imgName;
}
