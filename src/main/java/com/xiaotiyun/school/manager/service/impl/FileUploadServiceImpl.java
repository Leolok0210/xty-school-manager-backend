package com.xiaotiyun.school.manager.service.impl;

import cn.hutool.core.util.StrUtil;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.enums.FileTypeEnum;
import com.xiaotiyun.school.manager.service.FileUploadService;
import com.xiaotiyun.school.manager.basic.util.LocalUploaderUtil;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {
    @Resource
    private LocalUploaderUtil localUploaderUtil;

    @Override
    public String upload(byte[] fileResources, FileTypeEnum fileType, String fileName, Long schoolId) throws BusinessException {
        //判断资源内容
        if (null == fileResources || fileResources.length == 0) {
            throw new BusinessException(LanguageConstants.FILE_CONTENT_EMPTY);
        }
        if (StrUtil.isEmpty(fileName)) {
            throw new BusinessException(LanguageConstants.FILE_NAME_EMPTY);
        }
        return localUploaderUtil.putStreamToOSS(fileResources, fileName, fileType, schoolId);
    }
}
