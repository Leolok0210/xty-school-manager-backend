package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.SysFileEntity;
import org.springframework.web.multipart.MultipartFile;

public interface SysFileService extends IService<SysFileEntity> {

    Long addByStudent(MultipartFile file);

    Long add(MultipartFile file, Long schoolId);
}