package com.xiaotiyun.school.manager.service.impl;

import cn.hutool.core.io.FileUtil;
import com.xiaotiyun.school.manager.basic.enums.FileTypeEnum;
import com.xiaotiyun.school.manager.config.FileConfig;
import com.xiaotiyun.school.manager.model.entity.SchoolEntity;
import com.xiaotiyun.school.manager.service.FileService;
import com.xiaotiyun.school.manager.service.SchoolService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

@Slf4j
@Service
public class FileServiceImpl implements FileService {
    @Resource
    private SchoolService schoolService;
    @Resource
    private FileConfig fileConfig;

    @Override
    public void deleteUselessFile() {
        log.info("===============无用文件删除定时任务开始===============");
        try {
            List<SchoolEntity> schools = schoolService.list();
            if (CollectionUtils.isNotEmpty(schools)) {
                for (SchoolEntity school : schools) {
                    log.info("==============开始处理学校：" + school.getName() + "================");
                    for (FileTypeEnum value : FileTypeEnum.values()) {
                        if (value == FileTypeEnum.STUDENT_IMAGE_ZIP || value == FileTypeEnum.EXPORT) {
                            log.info("===============处理文件类型：" + value);
                            String handleFilePath = fileConfig.getFileRootPath() + File.separator + school.getId() + File.separator + value.getTypePath();
                            File handleFile = new File(handleFilePath);
                            if (handleFile.exists()) {
                                File[] files = handleFile.listFiles();
                                if (files != null) {
                                    for (File file : files) {
                                        if (!file.isDirectory()) {
                                            String fileName = file.getName();
                                            log.info("==============删除文件：" + fileName);
                                            FileUtil.del(file);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info("===============无用文件删除定时任务失败，失败原因：" + e.getMessage());
        }
        log.info("===============无用文件删除定时任务结束===============");
    }
}