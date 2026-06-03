package com.xiaotiyun.school.manager.service;

import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.enums.FileTypeEnum;

public interface FileUploadService {

    /**
     * 上传文件
     *
     * @param fileResources 文件资源
     * @param fileType      文件类型
     * @param fileName      文件名称，必须带后缀
     * @return
     */
    String upload(byte[] fileResources, FileTypeEnum fileType, String fileName, Long schoolId) throws BusinessException;
}
